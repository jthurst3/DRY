# removes unicode characters from Java files


iconv -c -f utf-8 -t ascii < $1 > $1_tmp
mv $1_tmp $1