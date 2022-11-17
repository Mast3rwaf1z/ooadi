from tkinter import *

parent = Tk()
parent.title("hello")
parent.geometry("500x250")

info = Label(text="Enter the credentials")
info.place(x=200, y=50)

username = Label(text="Username")
username.place(x=50, y=100)

textBox1 = Entry(parent, width=40, borderwidth=5)
textBox1.place(x=150, y=100)

password = Label(text="Password")
password.place(x=50, y=150)

textBox2 = Entry(parent, width=40, borderwidth=5)
textBox2.place(x=150, y=150)

def myClick():
    myLabel = Label(parent, text="Hello " + textBox1.get())
    myLabel.place(x=200, y=200)


log_in = Button(text="Log in", command=myClick)
log_in.place(x=400, y=200)

exit_button = Button(parent, text="Exit", command=parent.quit)
exit_button.place(x=50, y=200)

parent.mainloop()
