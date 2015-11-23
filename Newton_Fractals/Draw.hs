module Draw (Size, Scale, Coordinate, draw)  where 
import Graphics.GD

-- Coordinate in a complex plane.
type Coordinate = (Double, Double)

-- Boundary coordinates of a part of a complex plane, that we want to draw.
type Scale = (Coordinate, Coordinate) 


-- Given a size (Size = (Int, Int), this is type in Graphics.GD that determines the size of a picture) returns a list of all image pixels (Point = (Int, Int)).
pixels :: Size -> [Point]
pixels (sizex, sizey) = [(x,y) | x <- [0..sizex-1], y <- [0..sizey-1]]


-- Given a particular pixel, size of a picture and boundary coordinates, returns the coordinates corresponding to the pixel. 
toCoordinate :: Point -> Size -> Scale -> Coordinate
toCoordinate (x, y) (sizex, sizey) ((a, b), (c, d)) = 
	(a + (c-a)*tX , b + (d-b)*tY )
	where tX = fromIntegral x / fromIntegral sizex
	      tY = fromIntegral y / fromIntegral sizey


-- After being fed by picture size, boundary coordinates of a plotted part of a complex plane, name of the picture and function that assigns color to each pixel, this creates the picture. 
draw :: Size -> Scale -> String -> (Coordinate -> Color) -> IO ()
draw size scale name f = 
	do picture <- newImage size
	   mapM_ (g picture) $ pixels size 
	   savePngFile ("Output/" ++ name) picture
	where
	g picture p = (setPixel p (f(toCoordinate p size scale)) picture)
	
	   
