class Point:
   def __init__(self, x, y):
      self.x = x
      self.y = y

   def adjacent_to(self, other):
	   return ((self.x == other.x and abs(self.y - other.y) == 1) or
		      (self.y == other.y and abs(self.x - other.x) == 1))
