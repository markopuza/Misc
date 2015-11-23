module Main where
import Draw
import Data.Complex
import Graphics.GD

-- GLOBAL VARIABLES

-- This is the backbone of the produced fractal - the function we are applying newton method to.
function x =  cos $ x^2 - 1 

-- Just a derrivative of the above function. It, unfortunately, has to be filled in manually.
function' x =  (-2)*(sin $ x^2 - 1)*x

-- This is the number (>0) that determines, how many self-replications will be done. Usually, 3 is already computationally very demanding and doesn't create much difference in comparison to 2. When set to 1, algorithm will produce ordinary newton fractal.
rep :: Int
rep = 2 

-- The size of the generated picture.
iSize :: Size 
iSize = (160,160) 

-- Helper variable, that determines particular part of a complex plane that will be plotted.
h :: Double 
h = 10

-- These are the boundary coordinates of a complex plane that will be plotted. This scale is defined in this way, so that the plane origin is in the middle of a picture and distance of each corner pixel from origin is the same.
iScale :: Scale 
iScale = ((-h,h), (h,-h)) 

-- When the difference of old and new value is less than this, we stop the iteration
howClose :: Double
howClose = 1e-3 

-- After having reached this number, we assume that the point doesn't converge
maxIter :: Int
maxIter = 32

-- This number tells what is the ratio between original fractal and (each of) its self-replications (The bigger the number the smaller self-replications).
scaleRep :: Int
scaleRep = 2 

-- MAIN BODY 

-- The implementation of the Newton method. Takes complex number (and number of previous interations) and returns the pair (necessary number of iterations in order to converge, 
newton :: Complex Double -> Int -> (Int, Complex Double)
newton c i 
	| i > maxIter = (0,c)
	| (abs $ magnitude c - magnitude new) > howClose = newton new (i+1)
	| otherwise = (i, c)
	where new = c - function c / function' c

-- Self-replicating algorithm. Repeats newton method with the difference of the values of a point before and after current iteration.
replicateF :: Int -> Complex Double -> Int
replicateF rep z | rep == 1 = fst base
		 | otherwise = fst base + (replicateF (rep-1) (snd base - z)) `div` scaleRep
	where base = newton z 0 

-- So far, we assigned number of necessary iterations to each point. Function bellow assigns color to the number. For each element of rgb there is an arbitrary function used to evaluate it.
colorize :: Int -> Color
colorize n = rgb (cR n) (cG n) (cB n)
	where 
	cR x = x^2 - x ` div ` 2
	cG x = x^2 
	cB x = 2*x^2 

-- Assigns color to the coordinate based on its number.
colorNewton :: Coordinate -> Color 
colorNewton (x,y) = colorize $ replicateF rep (x :+ y)

-- Main function of a program, which carries out all the stuff and draws fractal. The lines bellow 'where' create function ID - number based on evaluating few values in function. This ID becomes part of the generated picture name, which is done in order to avoid overwriting pictures when experimenting with various functions. (It is not impossible  for two functions to have the same ID, however, it is improbable.
main :: IO () 
main = draw iSize iScale ("newton" ++ id ++ ".png") colorNewton
	where
	id = take 5 (v0 ++ v1 ++ v2 ++ v'1)
	(v0, v1, v2, v'1) = (f 0, f 1, f 2, f(-1))
	f = show.round.abs.(*3).function 

