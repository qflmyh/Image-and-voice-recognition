package com.example.imageandvoicerecognition.data.model

//API响应对象
data class TagDetail(val tag_name: String, val tag_confidence: Float)

data class Label(
    val Confidence: Int,
    val FirstCategory: String,
    val Name: String,
    val SecondCategory: String,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class TagListRes(
    val Labels: List<Label>,
    val RequestId: String,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class ProductInfo(
    val Confidence: Int,
    val Name: String,
    val Parents: String,
    val XMax: Int,
    val XMin: Int,
    val YMax: Int,
    val YMin: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class ProductListRes(
    val Products: List<ProductInfo>,
    val RequestId: String,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

// 定义Face与JSON结构匹配的数据类
data class HairInfo(
    val Bang: Int,
    val Color: Int,
    val length: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class CompletenessInfo(
    val Cheek: Int,
    val Chin: Int,
    val Eye: Int,
    val Eyebrow: Int,
    val Mouth: Int,
    val Nose: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class FaceQualityInfo(
    val Brightness: Int,
    val Completeness: CompletenessInfo,
    val Score: Int,
    val Sharpness: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class FaceAttributesInfo(
    val Age: Int,
    val Beauty: Int,
    val Expression: Int,
    val EyeOpen: Boolean,
    val Gender: Int,
    val Glass: Boolean,
    val Hair: HairInfo,
    val Hat: Boolean,
    val Mask: Boolean,
    val Pitch: Int,
    val Roll: Int,
    val Yaw: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class FaceInfo(
    val FaceAttributesInfo: FaceAttributesInfo,
    val FaceQualityInfo: FaceQualityInfo,
    val Height: Int,
    val Width: Int,
    val X: Int,
    val Y: Int,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

data class FaceDetectionResponse(
    val FaceInfos: List<FaceInfo>,
    val FaceModelVersion: String,
    val ImageHeight: Int,
    val ImageWidth: Int,
    val RequestId: String,
    val customizedParams: Map<String, Any>,
    val header: Map<String, Any>,
    val skipSign: Boolean
)

// 录音model
data class SpeechRecognitionResult(
    val transcript: String,
    val confidence: Float
)

//Ocr
data class DetectedItem(val confidence: Int, val detectedText: String)

data class DetectedTextInfo(
    val detectedText: String,
    val confidence: Long
)