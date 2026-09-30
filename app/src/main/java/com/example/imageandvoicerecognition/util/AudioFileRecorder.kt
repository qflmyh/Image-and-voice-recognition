package com.example.imageandvoicerecognition.util

import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder

import android.util.Log
import androidx.annotation.OptIn
import androidx.core.content.ContextCompat
import androidx.media3.common.audio.AudioProcessor
import androidx.media3.common.util.UnstableApi
import coil.size.Size
import com.tencentcloudapi.vm.v20200709.models.FileOutput

import java.io.File
import java.io.FileOutputStream


//音频录制工具类 AudioFileRecorder，主要功能是通过设备麦克风录制音频，将原始音频数据（PCM 格式）保存并转换为 WAV 格式（更通用的音频格式）
class AudioFileRecorder(private val context: Context) {
    // 未实际使用，预留的媒体录制器
    private var mediaRecorder: MediaRecorder? = null
    // 初始化时创建的音频文件（路径在外部存储）
    private var audioFile: File? = null
    // 用于录制原始PCM音频的核心类
    private var audioRecord: AudioRecord? = null
    // 标记是否正在录制
    private var isRecording = false
    // 存储原始PCM数据的文件
    private var pcmFile: File? = null
//    采样率：16kHz（常用的语音处理采样率）
    private val sampleRate = 16000  // 16kHz

//    初始化时设置音频文件路径：
    init {
        // 初始化时设置文件路径
        audioFile = File(context.getExternalFilesDir(null), "latest_audio.mp4")
        Log.d("Liang_file", audioFile.toString())
    }


//    开始录制（startRecording()）
//    权限检查：先检查是否有录音权限（RECORD_AUDIO），无权限则打印错误日志
//    配置 AudioRecord：
//    计算最小缓冲区大小（getMinBufferSize），参数包括采样率、单声道、16 位编码
//    初始化 AudioRecord，指定音频源（麦克风）、采样率、声道、编码格式和缓冲区大小
//    创建 PCM 文件：在应用外部存储目录创建 latest_audio.com（存储原始 PCM 数据）
//    启动录制线程：
//    标记 isRecording = true，调用 audioRecord.startRecording() 开始录制
//    启动子线程执行 writeAudioDataToFile，将麦克风数据写入 PCM 文件
    @OptIn(UnstableApi::class)
    fun startRecording() {
        if(ContextCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            try {
                val bufferSize = AudioRecord.getMinBufferSize(
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT
                )

                audioRecord = AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize
                )

                pcmFile = File(context.getExternalFilesDir(null) ,"latest_audio.com")
                isRecording = true
                audioRecord?.startRecording()

                val recordingThread = Thread {
                    writeAudioDataToFile(pcmFile, bufferSize)
                }
                recordingThread.start()
            } catch (e: Exception) {
                Log.e("AudioRecorder", "Failed to start media recorder", e)
            }
        }

        else {
            Log.e("AudioRecorder", "Context used is not an activity context, cannot request permission")
        }
    }

//    写入 PCM 数据（writeAudioDataToFile()）
//    在循环中读取麦克风数据（audioRecord.read），并写入 PCM 文件
//    循环条件为 isRecording，停止录制时会退出循环
//    使用 FileOutputStream 写入字节数据，确保资源自动释放（use 函数）
    private fun writeAudioDataToFile(file: File?, bufferSize: Int) {
        val data = ByteArray(bufferSize)
        FileOutputStream(file).use {
                fos ->
            while (isRecording) {
                val read = audioRecord?.read(data, 0, bufferSize) ?: 0
                if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                    fos.write(data, 0 , read)
                }
            }
        }
    }

//    停止录制（stopRecording()）
//    标记 isRecording = false，停止录制并释放 AudioRecord 资源
//    调用 convertPcmToWav 将 PCM 文件转换为 WAV 文件（latest_audio.wav）
    fun stopRecording() {
        isRecording = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        pcmFile?.let {
            convertPcmToWav(it, File(context.getExternalFilesDir(null), "latest_audio.wav"), getBufferSize())
        }
    }

//    getBufferSize()：获取音频录制的最小缓冲区大小（复用初始化时的参数）
    private fun getBufferSize(): Int {
        return AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )
    }

//    PCM 转 WAV 格式（convertPcmToWav()）
//    PCM 是原始音频数据（仅包含声音采样），而 WAV 是带头部信息的音频格式（头部包含采样率、声道等元数据）。转换步骤：
//    计算文件大小：WAV 文件大小 = PCM 数据大小 + 44 字节（WAV 头部）
//    构建 WAV 头部（44 字节）：
//    包含 "RIFF"、"WAVE" 等标识（用于识别文件格式）
//    采样率（16kHz）、声道数（单声道）、位深度（16 位）等音频参数
//    数据大小信息（PCM 数据的实际长度）
//    写入数据：
//    先将 44 字节头部写入 WAV 文件
//    再将 PCM 文件的原始数据写入 WAV 文件，完成格式转换
    @OptIn(UnstableApi::class)
    private fun convertPcmToWav(pcmFile: File, wavFile: File, bufferSize: Int) {
        val pcmSize = pcmFile.length()
        val wavSize = pcmSize + 36

        FileOutputStream(wavFile).use {
            fos ->
            val header = ByteArray(44)

            // WAV header
            // ChunkID
            header[0] = 'R'.code.toByte()
            header[1] = 'I'.code.toByte()
            header[2] = 'F'.code.toByte()
            header[3] = 'F'.code.toByte()
            // ChunkSize
            header[4] = (wavSize and 0xff).toByte()
            header[5] = ((wavSize shr 8) and 0xff).toByte()
            header[6] = ((wavSize shr 16) and 0xff).toByte()
            header[7] = ((wavSize shr 24) and 0xff).toByte()
            // Format
            header[8] = 'W'.code.toByte()
            header[9] = 'A'.code.toByte()
            header[10] = 'V'.code.toByte()
            header[11] = 'E'.code.toByte()
            // Subchunk1ID
            header[12] = 'f'.code.toByte()
            header[13] = 'm'.code.toByte()
            header[14] = 't'.code.toByte()
            header[15] = ' '.code.toByte()
            // Subchunk1Size
            header[16] = 16
            header[17] = 0
            header[18] = 0
            header[19] = 0
            // AudioFormat
            header[20] = 1
            header[21] = 0
            // NumChannels
            header[22] = 1
            header[23] = 0
            // SampleRate
            header[24] = (SAMPLE_RATE and 0xff).toByte()
            header[25] = ((SAMPLE_RATE shr 8) and 0xff).toByte()
            header[26] = ((SAMPLE_RATE shr 16) and 0xff).toByte()
            header[27] = ((SAMPLE_RATE shr 24) and 0xff).toByte()
            // ByteRate
            val byteRate = SAMPLE_RATE * 2 // 16 bit, Mono
            header[28] = (byteRate and 0xff).toByte()
            header[29] = ((byteRate shr 8) and 0xff).toByte()
            header[30] = ((byteRate shr 16) and 0xff).toByte()
            header[31] = ((byteRate shr 24) and 0xff).toByte()
            // BlockAlign
            header[32] = 2 // 16 bit, Mono
            header[33] = 0
            // BitsPerSample
            header[34] = 16
            header[35] = 0
            // Subchunk2ID
            header[36] = 'd'.code.toByte()
            header[37] = 'a'.code.toByte()
            header[38] = 't'.code.toByte()
            header[39] = 'a'.code.toByte()
            // Subchunk2Size
            header[40] = (pcmSize and 0xff).toByte()
            header[41] = ((pcmSize shr 8) and 0xff).toByte()
            header[42] = ((pcmSize shr 16) and 0xff).toByte()
            header[43] = ((pcmSize shr 24) and 0xff).toByte()

            fos.write(header, 0, 44)

            pcmFile.inputStream().use {
                fis ->
                val buffer = ByteArray(bufferSize)
                while(true) {
                    val read = fis.read(buffer)
                    if (read == -1) {
                        break
                    }
                    fos.write(buffer, 0, read)
                }
            }
        }
    }

//    getRecordedAudioFile()：返回转换后的 WAV 文件（latest_audio.wav），供外部使用
    fun getRecordedAudioFile(): File? {
        return pcmFile?.let {
            File(context.getExternalFilesDir(null), "latest_audio.wav")
        }
    }

    companion object {
        private const val SAMPLE_RATE = 16000   // 16kHz
    }
}


