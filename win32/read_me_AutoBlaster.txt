How to use AutoBlast 1.0

Explanation of config.txt:
Threads: There is a maximum thread count of 100 per IP, but increasing the thread count may not neccesary increase speed. Default is 20.
Program: This program was tested working with blastn and blastp, but the option of selecting other blast types is given.


The only file format this program uses for searching sequences is:

[Name]
>[Sequence ID]*

(Without the brackets.)

To run AutoBlast:
On Windows:
1. Click the .bat file
2. Open the FASTA formatted sequence file.
3. Program may take several hours (or even days, on longer files) to complete.

On other OSes:
1. Directly run the .jar file from the command line (directions vary by OS)
2. Select the file and proceed as with Windows.






