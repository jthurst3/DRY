#!/bin/bash

ssh jthurst3@cycle1.csug.rochester.edu 'cd Courses/CSC200/honors/report; rake'
scp jthurst3@cycle1.csug.rochester.edu:~/Courses/CSC200/honors/report/report.pdf .
open report.pdf
