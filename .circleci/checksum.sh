#!/usr/bin/env bash

# Usage: ./checksum.sh /path/to/checksum_output.txt
#
# Finds all pom.xml files and checksum them, dumping the aggregate checksum to provided file.
#
# Taken from https://github.com/chrisbanes/tivi/blob/c1219aeee9f62600fcd43d7caf1ea21e6e92930f/checksum.sh

RESULT_FILE=$1
if [ -f $RESULT_FILE ]; then
  rm $RESULT_FILE
fi
touch $RESULT_FILE

checksum_file() {
  echo `openssl md5 $1 | awk '{print $2}'`
}

FILES=()
while read -r -d ''; do
	FILES+=("$REPLY")
done < <(find . -name 'pom.xml' -type f -print0)

# Loop through files and append MD5 to result file
for FILE in ${FILES[@]}; do
	echo `checksum_file $FILE` >> $RESULT_FILE
done

# Now sort the file so that it is
sort $RESULT_FILE -o $RESULT_FILE
