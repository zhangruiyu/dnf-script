let img = images.read("./2.png")
//importClass(com.googlecode.tesseract.android.TessBaseAPI)
//新建OCR实例
//var tessocr = new TessBaseAPI()
// 新增：自定义模型路径(必须是绝对路径), files.path() 将相对路径转为绝对路径
//let myModelPath = files.path("./models");
let start = new Date()
// 识别图片中的文字，返回完整识别信息（兼容百度OCR格式）。
let result = gmlkit.ocr(img, "zh")
log('OCR识别耗时：' + (new Date() - start) + 'ms')
toastLog("完整识别信息: " + JSON.stringify(result))
//toastLog("文本识别信息: " + JSON.stringify(stringList))

// 回收图片
//img.recycle()
// 释放native内存，非必要，供万一出现内存泄露时使用
// paddle.release()