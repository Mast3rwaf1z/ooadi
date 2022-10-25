import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('localhost', 8000))
s.send('login alice test\n'.encode('utf-8'))
inp = ""
while not inp == "end":
    inp = input("> ")
    s.send(f'{inp}\n'.encode('utf-8'))