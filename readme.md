Edit Distance (v 1.0)
==========================
A Java program that solves the edit distance problem for two strings with input on two lines, one after the other. The program prints out a series of edit commands that will convert the first string into the second at minimum cost, and reports the total cost of such conversion.

Usage
-----
Compile
	javac EditDistance.java

Run
	java EditDistance

Costs and operations:
-----
0 Copy a character unchanged
6 Insert one, two, or three consecutive chars (any number up to three, same price)
3 Delete one, two, or three consecutive chars (any number up to three, same price)
7 Replace a single character with a different one
1 Permute two consecutive characters (e.g. turn 'xy' into 'yx')
2 Permute three consecutive chars (e.g. turn 'nda' into 'and')
0 AdjustBlanks convert any sequence of contiguous blanks to a different number of blanks as long as you have blanks in both strings.
1 ChangeCase replace a letter with its case-partner: A with a, or b with B, for instance