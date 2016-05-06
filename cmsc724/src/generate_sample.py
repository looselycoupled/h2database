'''
Creates authors.csv and authorpublications.csv for a given max_author_id
'''

import sys


def generate_authorpub_table(max_author_id):

	outfilename = './authorpublications_'+str(max_author_id)+'.csv'

	fout = open(outfilename,'w')

	with open('authorpublications.csv') as f:

		data = f.readlines()
		
		fout.write(data[0])

		for line in data[1:]:

			data = line.split('\n')[0]
			aid = int(data.split(',')[0].strip())
			pid = int(data.split(',')[1].strip())

			if aid<max_author_id:
				fout.write(str(aid)+","+str(pid)+"\n")

		fout.close()

	print("completed writing data to "+outfilename)

	return


def generate_authors_table(max_author_id):

	with open('./authors.csv') as f:

		authors = f.readlines()
		##this works as authors are stored sequentially
		reqd_authors = authors[:max_author_id+1]

	
	outfilename = './authors_'+str(max_author_id)+'.csv'
	with open(outfilename,'w') as out:
	
		for line in reqd_authors:
				out.write(line)


	print("completed writing data to "+outfilename)

	return 


if ( __name__ == "__main__"):

	if len(sys.argv)<2:
		print("Usage : python generate_sample.py max_author_id_value")
		sys.exit(0)

	max_author_id = int(sys.argv[1])

	generate_authors_table(max_author_id)
	generate_authorpub_table(max_author_id)
