from random import randint
from socket import AF_INET, SOCK_STREAM, socket
from sys import argv
import uuid
from time import sleep

address = argv[1] if len(argv) > 1 else input("Specify address: ")
id = argv[2] if len(argv) > 2 else str(uuid.uuid1())
password = argv[3] if len(argv) > 3 else input("Specify password: ")

sleep(.1) #a little bit of delay to make sure the server is running
s = socket(AF_INET, SOCK_STREAM)
print("Connecting to server...")
s.connect((address, 8888))
print("connected! validating id and password...")
s.send(bytes(password+" "+id+"\n", encoding="utf-8"))

if s.recv(1024).decode("utf-8") == "": 
    print("invalid id")
    exit(0)
print("Successfully connected to server")

sequence_number = 0
while True:
    sequence_number += 1
    print(f"[{sequence_number}] Server requested data")
    data = randint(-10, 10)
    s.send(bytes(str(data)+"\n", encoding="utf-8"))
    request = s.recv(1024).decode("utf-8")
    if request == "": break
    sleep(randint(0, 2)) #some delay to emulate a network