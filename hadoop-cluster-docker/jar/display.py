__author__ = 'qingyu'

def main():
    fileName = "displayOutPut"
    with open(fileName,'r') as f:
        lines = f.readlines()
    print "##########Total is ##########"
    print lines[0].split(' ')[0]
    print "##########Most Frequent bigrams####"
    for l in lines[1:-1]:
        print l
    print "##########10% needs topK #######"
    print lines[1].split(' ')[0]

if __name__ == "__main__":
    main()

