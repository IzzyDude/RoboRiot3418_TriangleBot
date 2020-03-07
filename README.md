# Moving Triangle Bot

To move the triangle bot use "MoveBot" in the "TriangleDrive" class. It has three variables that it will accept: X, Y, and Rotation. Any combination of these variables can be used to move the robot.

X sets the speed on the x axis that the robot moves. It is a double with a range of -1.0 to 1.0. This is because this is the range of the speed of the motors. Positive 1 is to the right, and -1 is to the left.

Y sets the speed on the y axis that the robot moves. It also is a double with a range of -1.0 to 1.0. Positive 1 is forwards, and -1 is backwards.

Rotation sets the speed at which the robot rotates. It has a range from -1 to +1. Positive 1 is clockwise, and -1 is counterclockwise. 

## How the code Works

```java
//Sets motor speeds
//Invert motor when moving forward and average with half of X speed and Rotation speed
        Motor1.set(((LY*-1)+(LX/Divide)+RX)/Average[0]);
//Average Forward speed with half of X speed and rotation speed
        Motor2.set((LY+(LX/Divide)+RX)/Average[1]);
//Average Inverted X speed with rotation speed 
        Motor3.set(((((LX)*-1)+RX)/Average[2]));

```

"Motor1" is the front left motor. "Motor2" is the front right motor. "Motor3" is the rear motor.

"Divide" = 2.

"Average" is the number of numbers for each motor that does not equal zero

Because Motor1 is at at a 135 degree angle and Motor2 is at a 45 degree angle, from a top down perspective, spinning both motors in opposite directions at the same speed will cancel out the Y component of their motion, leaving only the X component. However because movement of the motors is inverted when they are mounted onto the opposite side of a robot, the motors are moved in the same direction to move them in the opposite direction and vice versa. As for Motor 3, it already only has an X component and no Y component. Because the power of the two front motors has to equal the power of the one rear motor when moving left and right to move left and right, the X speed of the two front motors is divided by 2. Half of the force of two front motors plus the one rear motor equals the force on two motors. **The X speed of the robot for the motors is X/2 for Motor1 and Motor2  , and X for Motor3.** 

As for the Y movement, both front motors are moved in the same direction at the same speed, but not the third rear motor because it has no Y component.  **The Y speed of the robot for the motors is X for Motor1 and Motor2  , and 0 for Motor3.** 

Rotation is very simple. Just move all the motors in the same direction at the same speed.

**These result from these three problems are averaged together while ignoring any number that equals zero to find the speed for all three motors.**

$$
Motor1 = (-Y+X/2+Rotation)/Average1
$$

$$
Motor2 = (Y+X/2+Rotation)/Average2
$$

$$
Motor3=(-X+Rotation)/Average3
$$

# S-Curve Motion Profile for Rotation and Tracking

This function applies an s curve to an input ranging from -1 to +1. It does not however do this over time. Because of this if the input suddenly sets the motor speed to a high number rather than accelerating to it, the jerk will not be reduced. This function is intended to smooth out the motion of the robot when it is rotating to a position or tracking an object not to reduce jerk. However if the robot always accelerates to a position or speed rather than snapping to it this can be used to apply a s-curve to a linear motion profile. **It can be used with "S_CurveMotionProfile" and has 2 variables.** One is the input and the other is the scale. The scale affects the steepness of the curves.
