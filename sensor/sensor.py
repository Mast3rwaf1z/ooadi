from random import randint
from socket import AF_INET, SOCK_STREAM, socket
from sys import argv
from time import sleep

sleep(.1) #a little bit of delay to make sure the server is running
s = socket(AF_INET, SOCK_STREAM)
print("Connecting to server...")
s.connect((argv[1], 8888))

id = argv[2]
s.send(bytes(id+"\n", encoding="utf-8"))

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