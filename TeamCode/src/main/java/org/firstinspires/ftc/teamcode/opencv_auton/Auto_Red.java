/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.opencv_auton;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvInternalCamera;

import java.util.ArrayList;
@Disabled
@Autonomous
public class Auto_Red extends LinearOpMode {
    //private Blinker control_Hub;
    //private Blinker expansion_Hub_1;
    private DigitalChannel lF_Green;
    private DigitalChannel lF_Red;
    private DigitalChannel lR_Green;
    private DigitalChannel lR_Red;
    private DigitalChannel rF_Green;
    private DigitalChannel rF_Red;
    private DigitalChannel rR_Green;
    private DigitalChannel rR_Red;
    private DcMotor backleftMotor;
    private DcMotor backrightMotor;
    private DcMotor frontleftMotor;
    private DcMotor frontrightMotor;
    private Servo grab1;
    private Servo grab2;
    private DcMotor slide;

    double tgtPower = 0;

    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    //tag ID 1,2,3 from the 36h11 family
    int Left = 1;
    int Middle = 2;
    int Right = 3;

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode() {
        //control_Hub = hardwareMap.get(Blinker.class, "Control Hub");
        //expansion_Hub_1 = hardwareMap.get(Blinker.class, "Expansion Hub 1");
        lF_Green = hardwareMap.get(DigitalChannel.class, "LF_Green");
        lF_Red = hardwareMap.get(DigitalChannel.class, "LF_Red");
        lR_Green = hardwareMap.get(DigitalChannel.class, "LR_Green");
        lR_Red = hardwareMap.get(DigitalChannel.class, "LR_Red");
        rF_Green = hardwareMap.get(DigitalChannel.class, "RF_Green");
        rF_Red = hardwareMap.get(DigitalChannel.class, "RF_Red");
        rR_Green = hardwareMap.get(DigitalChannel.class, "RR_Green");
        rR_Red = hardwareMap.get(DigitalChannel.class, "RR_Red");
        backleftMotor = hardwareMap.get(DcMotor.class, "backleftMotor");
        backrightMotor = hardwareMap.get(DcMotor.class, "backrightMotor");
        frontleftMotor = hardwareMap.get(DcMotor.class, "frontleftMotor");
        frontrightMotor = hardwareMap.get(DcMotor.class, "frontrightMotor");
        grab1 = hardwareMap.get(Servo.class, "grab1");
        grab2 = hardwareMap.get(Servo.class, "grab2");
        slide = hardwareMap.get(DcMotor.class, "slide");

        //Encoders Init Start
        frontleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        frontrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backleftMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        backrightMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        frontleftMotor.setTargetPosition(0);
        frontrightMotor.setTargetPosition(0);
        backleftMotor.setTargetPosition(0);
        backrightMotor.setTargetPosition(0);

        frontleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backleftMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backrightMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //Encoders Init END


        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener()
        {
            @Override
            public void onOpened()
            {
                camera.startStreaming(800,448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode)
            {

            }
        });

        telemetry.setMsTransmissionInterval(50);

        /*
         * The INIT-loop:
         * This REPLACES waitForStart!
         */
        while (!isStarted() && !isStopRequested())
        {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if(currentDetections.size() != 0)
            {
                boolean tagFound = false;

                for(AprilTagDetection tag : currentDetections)
                {
                    if(tag.id == Left || tag.id == Middle || tag.id == Right)
                    {
                        tagOfInterest = tag;
                        tagFound = true;
                        break;
                    }
                }

                if(tagFound)
                {
                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                    tagToTelemetry(tagOfInterest);
                }
                else
                {
                    telemetry.addLine("Don't see tag of interest :(");

                    if(tagOfInterest == null)
                    {
                        telemetry.addLine("(The tag has never been seen)");
                    }
                    else
                    {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }
                }

            }
            else
            {
                telemetry.addLine("Don't see tag of interest :(");

                if(tagOfInterest == null)
                {
                    telemetry.addLine("(The tag has never been seen)");
                }
                else
                {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }

            }

            telemetry.update();
            sleep(20);
        }

        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */

        /* Update the telemetry */
        if(tagOfInterest != null)
        {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        }
        else
        {
            telemetry.addLine("Warning: No tag was found! Last resort code will be ran for a 33% chance of succession :(");
            telemetry.update();
        }


        while(opModeIsActive()) {
            // Autonomous Code
            if (tagOfInterest.id == Left) {
                //left code
                grab1.setPosition(.90);
                grab2.setPosition(.30);
                sleep(2000);
                slide.setPower(-1);
                sleep(2000);
                slide.setPower(0);
                forward(28, .25);
                left(32,.75);
                //forward(4,.25);
                slide.setPower(1);
                sleep(1600);
                slide.setPower(0);
                terminateOpModeNow();
            } else if (tagOfInterest == null || tagOfInterest.id == Middle) {
                //middle code
                grab1.setPosition(.90);
                grab2.setPosition(.30);
                sleep(2000);
                slide.setPower(-1);
                sleep(2000);
                slide.setPower(0);
                forward(34, .25);
                //26
                backwards(8,.25);
                slide.setPower(1);
                sleep(1600);
                slide.setPower(0);
                terminateOpModeNow();
            } else if (tagOfInterest.id == Right) {
                //right code
                grab1.setPosition(.90);
                grab2.setPosition(.30);
                sleep(2000);
                slide.setPower(-1);
                sleep(2000);
                slide.setPower(0);
                forward(26, .25);
                right(32,.75);
                slide.setPower(1);
                sleep(1600);
                slide.setPower(0);
                terminateOpModeNow();
            }
        }

        /* You wouldn't have this in your autonomous, this is just to prevent the sample from ending */
        //while (opModeIsActive()) {sleep(20);}
    }


    void forward(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(30/23)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(30/23)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(30/23)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(30/23)));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(1000);
    }

    void backwards(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(30/23)));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)(distance*103.6/7.42109*(30/23)));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(30/23)));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)(distance*103.6/7.42109*(30/23)));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(1000);
    }
    void left(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(30/23))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(30/23))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(30/23))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(30/23))));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
        }
        sleep(2000);
    }

    void right(double distance, double power ){

        frontleftMotor.setTargetPosition(frontleftMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(30/23))));
        backleftMotor.setTargetPosition(backleftMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(30/23))));
        frontrightMotor.setTargetPosition(frontrightMotor.getTargetPosition()-(int)((distance*(103.6/7.42109)*(30/23))));
        backrightMotor.setTargetPosition(backrightMotor.getTargetPosition()+(int)((distance*(103.6/7.42109)*(30/23))));
        frontleftMotor.setPower(power);
        frontrightMotor.setPower(power);
        backleftMotor.setPower(power);
        backrightMotor.setPower(power);
        while(frontleftMotor.isBusy() && backleftMotor.isBusy() && frontrightMotor.isBusy() && backrightMotor.isBusy()){
            updatedTelemetry();
            telemetry.update();
        }
        sleep(2000);
    }
    void tagToTelemetry(AprilTagDetection detection)
    {
        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y*FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z*FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }
    void updatedTelemetry(){
        telemetry.addData("Status", "Running");
        telemetry.addData("FL", frontleftMotor.getCurrentPosition());
        telemetry.addData("FR", frontrightMotor.getCurrentPosition());
        telemetry.addData("BL", backleftMotor.getCurrentPosition());
        telemetry.addData("BR", backrightMotor.getCurrentPosition());
        telemetry.update();
    }


}

