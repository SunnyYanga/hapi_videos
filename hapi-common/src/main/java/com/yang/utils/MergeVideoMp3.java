package com.yang.utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yg
 * @date 2020/8/12 19:41
 */
public class MergeVideoMp3 {

    private String ffmpegexe;

    public MergeVideoMp3(String ffmpegexe) {
        this.ffmpegexe = ffmpegexe;
    }

    public String convertor1(String videoInputPath, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 1.mp4 -vcodec copy -an 2.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegexe);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-vcodec");

        command.add("copy");
        command.add("-an");

        command.add(videoOutputPath);


        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }


        return videoOutputPath;

    }

    public void convertor2(String videoInputPath, String mp3InputPath, double seconds, String videoOutputPath) throws Exception {
//		ffmpeg.exe -i 2.mp4 -i 2.mp3 -t 10 -y 3.mp4
        List<String> command = new ArrayList<>();
        command.add(ffmpegexe);

        command.add("-i");
        command.add(videoInputPath);

        command.add("-i");
        command.add(mp3InputPath);

        command.add("-t");

        command.add(String.valueOf(seconds));

        command.add("-y");

        command.add(videoOutputPath);


        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        InputStream errorStream = process.getErrorStream();
        InputStreamReader inputStreamReader = new InputStreamReader(errorStream);
        BufferedReader br = new BufferedReader(inputStreamReader);

        String line = "";
        while ( (line = br.readLine()) != null ) {
        }

        if (br != null) {
            br.close();
        }
        if (inputStreamReader != null) {
            inputStreamReader.close();
        }
        if (errorStream != null) {
            errorStream.close();
        }

    }

    public static void main(String[] args) {
        MergeVideoMp3 ffmpeg = new MergeVideoMp3("D:\\apps\\ffmpeg\\bin\\ffmpeg.exe");
        try {
            String convertor1 = ffmpeg.convertor1("D:\\apps\\ffmpeg\\bin\\1.mp4", "D:\\apps\\ffmpeg\\bin\\2.mp4");
            ffmpeg.convertor2(convertor1, "D:\\apps\\ffmpeg\\bin\\2.mp3", 9, "D:\\apps\\ffmpeg\\bin\\3.mp4");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
