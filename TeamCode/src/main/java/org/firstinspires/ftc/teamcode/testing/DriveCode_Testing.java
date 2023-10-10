
/*
Copyright 2022 FIRST Tech Challenge Team 20177

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.Blinker;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gyroscope;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

/**
 * This file contains an minimal example of a Linear "OpMode". An OpMode is a 'program' that runs in either
 * the autonomous or the teleop period of an FTC match. The names of OpModes appear on the menu
 * of the FTC Driver Station. When an selection is made from the menu, the corresponding OpMode
 * class is instantiated on the Robot Controller and executed.
 *
 * This particular OpMode just executes a basic Tank Drive Teleop for a PushBot
 * It includes all the skeletal structure that all linear OpModes contain.
 *
 * Remove a @Disabled the on the next line or two (if present) to add this opmode to the Driver Station OpMode list,
 * or add a @Disabled annotation to prevent this OpMode from being added to the Driver Station
 */
@TeleOp

public class DriveCode_Testing extends LinearOpMode {
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
    double clawupdate;
    boolean slow = true;

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


        telemetry.addData("Status", "Initialized");
        telemetry.update();
        // Wait for the game to start (driver presses PLAY)
        //claw.setPosition(0);
        waitForStart();
        // run until the end of the match (driver presses STOP)
        grab1.setPosition(0);
        grab2.setPosition(40);

        rF_Green.setMode(DigitalChannel.Mode.OUTPUT);
        rR_Green.setMode(DigitalChannel.Mode.OUTPUT);
        lR_Green.setMode(DigitalChannel.Mode.OUTPUT);
        lF_Green.setMode(DigitalChannel.Mode.OUTPUT);
        rF_Red.setMode(DigitalChannel.Mode.OUTPUT);
        rR_Red.setMode(DigitalChannel.Mode.OUTPUT);
        lR_Red.setMode(DigitalChannel.Mode.OUTPUT);
        lF_Red.setMode(DigitalChannel.Mode.OUTPUT);

        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            //telemetry.update();
            telemetry.addData("Encoder value", slide.getCurrentPosition());
            telemetry.update();

            double r = Math.hypot(-gamepad1.left_stick_x, gamepad1.left_stick_y);
            double robotAngle = Math.atan2(gamepad1.left_stick_y, -gamepad1.left_stick_x) - Math.PI / 4;
            //these had 'final' before them at one point "final double v1 = r * Math.cos(robotangle) + rightx"
            //-Team 15036
            //Set drive speed

            if (gamepad1.right_bumper) {
                slow = true;
            }else{
                slow = false;
            }

            if (gamepad1.left_bumper) {
                slow = false;
            }else{
                slow = true;
            }


            if (slow = true) {
                double rightx = gamepad1.right_stick_x * .25;
                double v1 = (r * Math.cos(robotAngle)) * .25 - rightx;
                double v2 = (r * Math.sin(robotAngle)) * .25 + rightx;
                double v3 = (r * Math.sin(robotAngle)) * .25 - rightx;
                double v4 = (r * Math.cos(robotAngle)) * .25 + rightx;
                frontleftMotor.setPower(v1);
                frontrightMotor.setPower(-v2);
                backleftMotor.setPower(v3);
                backrightMotor.setPower(-v4);

                rF_Red.setState(true);
                rR_Red.setState(true);
                lR_Red.setState(true);
                lF_Red.setState(true);

                rF_Green.setState(false);
                rR_Green.setState(false);
                lR_Green.setState(false);
                lF_Green.setState(false);

            } else {
                double rightx = gamepad1.right_stick_x * .45;
                double v1 = (r * Math.cos(robotAngle)) * .75 - rightx;
                double v2 = (r * Math.sin(robotAngle)) * .75 + rightx;
                double v3 = (r * Math.sin(robotAngle)) * .75 - rightx;
                double v4 = (r * Math.cos(robotAngle)) * .75 + rightx;
                frontleftMotor.setPower(v1);
                frontrightMotor.setPower(-v2);
                backleftMotor.setPower(v3);
                backrightMotor.setPower(-v4);

                rF_Green.setState(true);
                rR_Green.setState(true);
                lR_Green.setState(true);
                lF_Green.setState(true);

                rF_Red.setState(false);
                rR_Red.setState(false);
                lR_Red.setState(false);
                lF_Red.setState(false);
            }

            tgtPower = this.gamepad1.left_trigger;
            slide.setPower(-tgtPower);
            tgtPower = this.gamepad1.right_trigger;
            slide.setPower(tgtPower);


            if (gamepad1.a) {
                grab1.setPosition(0);
                grab2.setPosition(40);
            } else {
                if (gamepad1.b) {
                    grab1.setPosition(.40);
                    grab2.setPosition(0);
                } else {
                }

                //END OF CODE

            }

        }
    }

}





























