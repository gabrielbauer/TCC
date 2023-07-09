#! /bin/bash
rm listajogos.txt
find . -maxdepth 1 -name "*.lud" -type f -printf '%f\n' > listajogos.txt
