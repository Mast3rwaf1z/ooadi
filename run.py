#!/bin/python3.10
from sys import argv
from os import system
from time import sleep

if argv[1] == "exit":
    system("tmux kill-session")
    exit(0)

server_ip = "localhost"
ids = [i for i in range(1, int(argv[1]) + 1)]

system(f'make init sensorCount={argv[1]}')

system('tmux new-session -s "session" -d')
system('tmux send "make server" ENTER')
sleep(2)
for id in ids:
    system('tmux split-window -h')
    system(f'tmux send "make sensor arg1={server_ip}" ENTER')

system("tmux attach")
