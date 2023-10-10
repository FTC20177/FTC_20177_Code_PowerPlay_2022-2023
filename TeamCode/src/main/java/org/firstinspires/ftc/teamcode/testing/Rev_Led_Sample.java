package org.firstinspires.ftc.teamcode.testing;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.LED;
import com.qualcomm.robotcore.hardware.DigitalChannel;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

@Disabled
@TeleOp

public class Rev_Led_Sample extends LinearOpMode {
    private DigitalChannel touch;
    private DigitalChannel redLED;
    private DigitalChannel greenLED;


    @Override
    public void runOpMode() {
        // Get the LED colors and touch sensor from the hardwaremap
        redLED = hardwareMap.get(DigitalChannel.class, "red");
        greenLED = hardwareMap.get(DigitalChannel.class, "green");
        touch = hardwareMap.get(DigitalChannel.class, "touch");

        // Wait for the play button to be pressed
        waitForStart();

        // change LED mode from input to output
        redLED.setMode(DigitalChannel.Mode.OUTPUT);
        greenLED.setMode(DigitalChannel.Mode.OUTPUT);

        // Loop while the Op Mode is running
        while (opModeIsActive()) {
            if (touch.getState()){
                //Touch Sensor is not pressed
                greenLED.setState(false);
                redLED.setState(true);

            } else {
                //Touch Sensor is pressed
                redLED.setState(false);
                greenLED.setState(true);
            }

        }
    }
}