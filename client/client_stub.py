import socket
s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
s.connect(('localhost', 8000))
s.settimeout(5)
s.send(f'login {input("enter username: ")} {input("enter password: ")}\n'.encode('utf-8'))
inp = ""
recv = s.recv(1024).decode("utf-8")
if recv == "failed":
    print("Failed to log in")
    exit(0)
print("Connected!")
while True:
    inp = input("> ")
    s.send(f'{inp}\n'.encode('utf-8'))
    if inp == "end": break
    try:
        recv = s.recv(1024).decode("utf-8")
        response = recv[:recv.find(":")]
        data = recv[recv.find(":")+1:]
        match(response):
            case "getdatareply":
                print(data)
            case "getrangereply":
                print(data)
            case "getidsreply":
                print(data)
            case _:
                print(recv)
    except TimeoutError as e:
        print(f"timed out, command [{inp}] was probably invalid")
s.close()