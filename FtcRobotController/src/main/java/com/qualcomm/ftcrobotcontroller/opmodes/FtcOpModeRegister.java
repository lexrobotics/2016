/* Copyright (c) 2014, 2015 Qualcomm Technologies Inc

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Qualcomm Technologies Inc nor the names of its contributors
may be used to endorse or promote products derived from this software without
specific prior written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.eventloop.opmode.OpModeRegister;

import lib.TeleOp;

/**
 * Register Op Modes
 */
public class FtcOpModeRegister implements OpModeRegister {

    /**
     * The Op Mode Manager will call this method when it wants a list of all
     * available op modes. Add your op mode to the list to enable it.
     *
     */
    public void register(OpModeManager manager) {

    /*00
     * register your op modes here.
     * The first parameter is the name of the op mode
     * The second parameter is the op mode class property
     *
     * If two or more op modes are registered with the same name, the app will display an error.

    /*
     * The NullOp op mode
     */
        manager.register("NullOp", NullOp.class);

        manager.register("-----", NullOp.class);

        manager.register("Red", RedPath.class);
        manager.register("RedScoot", RedPathScoot.class);
        manager.register("RedWide", RedPathWide.class);
        manager.register("RedWideScoot", RedPathWideScoot.class);

        manager.register("------", NullOp.class);

        manager.register("RedDefensive", DefensiveRedPath.class);
        manager.register("BlueDefensive", DefensiveBluePath.class);
        manager.register("RedWideDefensive", DefensiveRedWidePath.class);
        manager.register("BlueWideDefensive", DefensiveBlueWidePath.class);

        manager.register("-------", NullOp.class);

        manager.register("Blue", BluePath.class);
        manager.register("BlueScoot", BluePathScoot.class);
        manager.register("BlueWide", BluePathWide.class);
        manager.register("BlueWideScoot", BluePathWideScoot.class);

        manager.register("--------", NullOp.class);

        manager.register("TeleOpRed", TeleOpRed.class);
        manager.register("TeleOpBlue", TeleOpBlue.class);

        manager.register("--------", NullOp.class);

        manager.register("SensorPrint", SensorPrint.class);


        //manager.register("pidCalibration", pidCalibration.class);
        //manager.register("servoCalibration", servoCalibration.class);
        //manager.register("TwoWheelDrive", dimitriIsAWhinyChief.class);
        //manager.register("I2CMuxTest", I2CMuxTest.class);
        //manager.register("TeleOp", TeleOp.class);
        //manager.register("BluePathWideNoTurn", BluePathWideNoTurn.class);
        //manager.register("tillWhiteTest", tillWhiteTest.class);
        //manager.register("IMUTest", IMUTest.class);

    }
}
