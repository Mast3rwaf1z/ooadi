from random import randint
from socket import AF_INET, SOCK_STREAM, socket
from sys import argv
from threading import Thread

s = socket(AF_INET, SOCK_STREAM)
s.connect((argv[1], 8888))

arg = ""
def getarg():
    while True: 
        global arg
        arg = input("exit? (y/N): ")
t = Thread(target=getarg, daemon=True)

while not arg == "y" or not arg == "Y":
    request = s.recv(1024).decode("utf-8")
    data = randint(-10, 10)
    s.send(str(data))