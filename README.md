A Java-based implementation of an L-System generator.




An example L-System "program":

   AXIOM: F
   F -> FF[[-FFF@]++FF@]


Supported instructions:


	  F	  move pen forward and draw a line
	  f	  move pen forward w/o drawing a line
	  +	  turn left by default degrees
	  -	  turn right by default degrees
	  [	  push current state (x/y position, angle) on to the context stack
	  ]	  pop context from stack and discard/replace current state (x/y postion, angle)
	  @	  draw a 'dot' with a specified radius (default is 1/2 current line length) 

To build the Elses interpreter using Ant:

    ant clean build
    
    
To run from the build directory:

java -cp dist org.hccp.elses.Elses <options> <lsys-program-input-file> <output-file>


Options:

--angle,-a ANGLE      The angle to increment/decrement when interpreting '+' or
                      '-' commands. A range of angle values can be given for the
                      interpreter to randomly chooose from at each step by using
                      the form of 'n-m'. For example the argument -a 20-35 would
                      allow the interpreter to choose values between 20 degrees
                      and 35 degrees at each step. The default value, if no
                      angle or range is passed via this argument, is 45 degrees.

--help,-h      Prints this message.

--iterations,-i ITERATIONS      The number of rule-application iterations.

--line-length,-l LINE_LENGTH      The length of each pen move 'F'. A range of
                                  line length values can be given for the
                                  interpreter to randomly chooose from at each
                                  step by using the form of 'n-m'. For example
                                  the argument -l 2-10 would allow the
                                  interpreter to choose values between 2 and 10
                                  at each step. If no line length is specified,
                                  a default line length of 10 will be used.

--x-position,-x INITIAL_X_POSITION      The initial x position from which to
                                        begin drawing. The default is 150 (the
                                        center point for a landscape-oriented A3
                                        paper.

--y-position,-y INITIAL_Y_POSITION      The initial y position from which to
                                        begin drawing. The default is 25.

--dot-radius,-r DOT_RADIUS      The radius of the circle drawn by a '@' command.
                                If no radius is selected, a default radius of
                                1/2 of the current line length will be used.

--width,-w WIDTH      Sets the width of the draw-able page to the provided
                      value. The default is 291 as this matches the width of an
                      A3 paper in portrait mode.

--height HEIGHT      Sets the height of the draw-able page to the provided
                     value. The default is 420 as this matches the height of an
                     A3 paper in portrait mode.




Example:

For a file, test-003.txt, that cntains the following content:

    AXIOM: F
    F -> FF[[-FFF@]++FF@]

You can run the following command:
    java -cp dist org.hccp.elses.Elses -i 2 -a 10 -r 5 -l 25 -x 150 -y 50 samples/test-003.txt /tmp/lsys-plot.svg

in order to generate an SVG like this:

<img src="./samples/test-003.svg" border="10"/>






Credits:

A bunch of the parsing code and structure is derived from Lox and Robert Nystrom's wonderful book "Crafting Interpreters".
