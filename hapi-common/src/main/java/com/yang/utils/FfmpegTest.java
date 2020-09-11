package com.yang.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yg
 * @date 2020/8/12 17:29
 */
public class FfmpegTest {

    private String ffmpegExe;

    public FfmpegTest(String ffmpegExe) {
        this.ffmpegExe = ffmpegExe;
    }

    public void  convertor(String videoInputPath, String videoOutputPath) throws IOException {
        List<String> command = new ArrayList<>();
        command.add(ffmpegExe);
        command.add("-i");
        command.add(videoInputPath);
        command.add(videoOutputPath);
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();
        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {

        }

        if (bufferedReader != null) {
            bufferedReader.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
        FfmpegTest ffmpegTest = new FfmpegTest("D:\\apps\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            ffmpegTest.convertor("D:\\downloads\\coverr--06-20-boy-in-misty-mountain-01-9186.mp4", "D:\\11.avi");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
