This directory contains my entry to functional programming competition for year 2014.
It consists of two two modules - Draw.hs and NewtonFlowers.hs and directory Output.
Draw.hs lists a couple of custom-defined types and functions related to drawing and 
NewtonFlowers.hs contains a bunch of functions and literals that determine what to draw.

The main purpose of the program is to plot nice fractals created with slightly modified algorithm for newton's method.

----- How to use this -----

First, NewtonFlowers.hs depends on the library Graphics.GD, so it is necessary to install it.
In my case, this was just a matter of:
	cabal install gd
	sudo apt-get install libgd2-xpm-dev

---

After having everything set up, open file NewtonFlowers.hs. Right at the beginning, there are
some variables that our picture is based upon. 
When playing around with them, the most important one is "function". Fill it in with arbitrary combination of polynomial and goniometric functions. Do not forget, however, to change the variable "function'" (this is just a derrivative) accordingly.

Next, there is variable "rep". When set to 1, we will get an ordinary newton's fractal. But in case of greater integer, the modified algorithm creates (kind of) smaller copies of the original fractal in itself, centered in certain points, so our picture gets to be much richer. The recommended value is 2, 
greater integers are computationally demanding and pictures become too crowded.

There is also the tuple "iSize", which simply determines picture size. As drawing large pictures 
takes non-trivial ammount of time, choose this one carefully. (160x160 takes about 20 seconds on my machine)

Variable "h" determines the size of the part of complex plane we are drawing. In other words, this is just 'zoom' - the smaller h the bigger zoom.

It is recommended not to change other variables. (Anyway - one can play a bit with coloring function
"colorize" in order to find a nice pallette)

---
How to draw:
 - Compile NewtonFlowers.hs
 - type "main" and run it
 ---> your picture should be created in the directory "Output" and named "newton{somenumber}".

----------------------------

Marko Puza

