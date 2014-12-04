#!/bin/bash

ssh jthurst3@cycle1.csug.rochester.edu 'cd Courses/CSC200/honors/report; ./compiletex.sh'
scp jthurst3@cycle1.csug.rochester.edu:~/Courses/CSC200/cs200repo/honors/report/report.pdf .
open assign8.pdf
