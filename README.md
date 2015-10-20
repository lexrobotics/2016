# 2 Bits and a Byte 2016 FTC Repo

Javadoc: [https://rawgit.com/ftctechnh/ftc_app/master/doc/javadoc/com/qualcomm/robotcore/hardware/package-summary.html](https://rawgit.com/ftctechnh/ftc_app/master/doc/javadoc/com/qualcomm/robotcore/hardware/package-summary.html)
## Phone Configuration
Setup: [https://www.youtube.com/watch?v=n597U6rcl2Y](https://www.youtube.com/watch?v=n597U6rcl2Y)
TL;DW:
- Remove the phone SIM card
- Turn on airplane mode, then enable WiFi
- Turn off display auto-rotation
- Disable notification for Google Play Services

Wireless Debugging: [https://www.youtube.com/watch?v=0XZ6EH7BV2M](https://www.youtube.com/watch?v=0XZ6EH7BV2M)

## Troubleshooting
- The robot controller cannot detect USB devices
  - Ensure that the robot battery voltage is higher than 13.2V.
  - Ensure that each USB device is properly receiving power (should be lit up, either red or green)
  - If your setup is correct, do a full power cycle and disconnect all devices. Suggested order for powering on the robot: 1) Phone on, no apps running 2) Plug in power distribution module 3) Turn on power distribution module, allowing the phone to launch the robot controller app automatically.
