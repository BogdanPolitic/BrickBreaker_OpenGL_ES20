# BrickBreaker_OpenGL_ES20_AndroidStudio [[Play Store link](https://play.google.com/store/apps/details?id=co.snOmOtiOn.bogdan.catchthejellyv112020)]

<h3> General description </h3>

Like many break brick'ers you saw in your childhood, this is a fun and relaxing game in which you're in charge of shooting bricks and get the rewards underneath in order to  progress further. There's a limited number of supply balls as shown in the bottom left. The game also accounts your current and highest score points.

![Demo](https://github.com/BogdanPolitic/Demos/blob/main/Break-the-Bricks-short-demo.gif?raw=true)


<h3> Implementation details </h3>

The game is made in Android Studio, using Java and OpenGL ES 2.0. There's only one main window (activity) which contains a GLSurfaceView with a GLRenderer in charge of rendering every frame.

In order to quickly implement the game's idea and to separate the logic and the design, I made a tiny game engine serving as a factory of shapes. Each shape has its own class, but they belong to the same super class Shape. The super class has a general draw method which calls the glDrawElements() with the shape's appropriate buffers.

In order to create an eye-delighting game design, there are also several types of animations implemented: 
- the ball motion
- the brick's destruction
- the platform motion
- the powerup motion and collision with the platform
- the text showing score points
- a white gradient line repetitive motion over the brick network

Each animation is based on a self-implemented timer (InterpolationTimer class) which spans all its life cycle and outputs the interpolation percentage (between 0 and 1) between the initial transform and the final transform.
