package info.xingxingdd.dnf.executor.ext;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;

import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import info.xingxingdd.dnf.assistant.DetectionAssistant;
import info.xingxingdd.dnf.assistant.ScreenCapture;
import info.xingxingdd.dnf.assistant.ScreenCaptureTask;
import info.xingxingdd.dnf.executor.AbstractAsyncMessageExecutor;
import info.xingxingdd.dnf.server.message.Input;
import info.xingxingdd.dnf.server.message.Output;
import info.xingxingdd.dnf.utils.BitmapUtils;
import info.xingxingdd.yolov5.library.YoloV5Ncnn;

public class DetectionMessageExecutor extends AbstractAsyncMessageExecutor {

    public static int index = 0;

    private final String screenshotFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();


    @Override
    protected void doAsyncProcess(Input input) {

        ScreenCaptureTask screenCaptureTask = new ScreenCaptureTask(input.getRequestId()) {

            @Override
            protected boolean process(Bitmap screenshot) {
                try {
                    YoloV5Ncnn.Obj[] targets = DetectionAssistant.yoloV5Ncnn.detect(screenshot, false);
                    Map<String, Object> data = new HashMap<>();
                    if (targets != null && targets.length > 0) {
                        data.putAll(Arrays.stream(targets).collect(Collectors.groupingBy(obj -> obj.label)));
                    }
                    Output output = Output.success();
                    output.setRequestId(getRequestId());
                    output.setData(data);
                    connectionManager.send(output);
//                    if (targets != null && targets.length > 0) {
//                        saveResult(screenshot, targets);
//                        saveOrigin(screenshot);
//                        index = index + 1;
//                    }
                    Log.i("dnf-server", "识别到目标:" + new Gson().toJson(targets));
                } catch (Exception e) {
                    Log.e("dnf-server", "生成截图文件异常: " + e.getLocalizedMessage());
                }
                return true;
            }

        };
        ScreenCapture.getInstance().addScreenCaptureTask(screenCaptureTask);
    }

    private void saveResult(Bitmap bitmap, YoloV5Ncnn.Obj[] targets) {
        try(FileOutputStream fos = new FileOutputStream(screenshotFileDir + "/dnf-server-detect" + index + ".jpeg")) {
            Bitmap target = BitmapUtils.showObjects(bitmap, targets);
            target.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            Log.e("dnf-server", "生成截图文件异常: " + e.getLocalizedMessage());
        }
    }

    private void saveOrigin(Bitmap bitmap) {
        try(FileOutputStream fos = new FileOutputStream(screenshotFileDir + "/dnf-server" + index + ".jpeg")) {
            Log.i("dnf-server", screenshotFileDir + "/dnf-server" + index + ".jpeg");
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            Log.e("dnf-server", "生成截图文件异常: " + e.getLocalizedMessage());
        }
    }
}
