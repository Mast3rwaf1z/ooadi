from tkinter import *
import socket


class Account:
    def __init__(self, username, password):
        self.username = username
        self.password = password

    def getUsername(self):
        return self.username

    def setUsername(self, username):
        self.username = username

    def getPassword(self):
        return self.password

    def setPassword(self, password):
        self.password = password


def main():
    acc = Account("Bob", "1234")

    window = Tk()
    window.title("Totally legit sensor app")
    window.geometry("600x600")
    window.configure(bg='#2F2D2E')

    frame1 = Frame(window, width=500, height=200, bg='#84A9C0')
    frame1.place(x=50, y=200)

    info = Label(frame1, text="Enter the credentials", bg='#84A9C0')
    info.place(x=200, y=10)

    usernameLabel = Label(frame1, text="Username", bg='#84A9C0')
    usernameLabel.place(x=10, y=50)

    usernameTextBox = Entry(frame1, width=50, borderwidth=1)
    usernameTextBox.place(x=100, y=50)

    passwordLabel = Label(frame1, text="Password", bg='#84A9C0')
    passwordLabel.place(x=10, y=100)

    passwordTextBox = Entry(frame1, width=50, borderwidth=1)
    passwordTextBox.place(x=100, y=100)

    myLabel = Label(frame1)

    def myClick():
        username = usernameTextBox.get()
        password = passwordTextBox.get()
        print(username)
        print(password)
        
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        s.connect(('localhost', 8000))
        s.send(f'login {username} {password}\n'.encode('utf-8'))

        recv = s.recv(1024).decode("utf-8")
        if recv == "failed":
            print("Failed to log in")
            exit(0)

        if username == acc.getUsername() and password == acc.getPassword():
            myLabel.configure(text="Log in successful", fg='green', bg='#84A9C0')
            myLabel.place(x=150, y=150)
            window.after(2000, frame1.destroy)
            log_in['state'] = DISABLED
            exit_button['state'] = DISABLED
            window.after(2000, lambda: MainMenu(window))
        else:
            myLabel.configure(text="Log in unsuccessful", fg='red', bg='#84A9C0')
            myLabel.place(x=150, y=150)

    # log in button
    log_in = Button(frame1, text="Log in", command=myClick, bg='#84A9C0')
    log_in.place(x=380, y=150)

    exit_button = Button(frame1, text="Exit", command=window.quit, bg='#84A9C0')
    exit_button.place(x=50, y=150)

    window.mainloop()


def MainMenu(window):
    # new canvas
    frameMenu = Frame(window, width=600, height=600)
    frameMenu.configure(bg='#84A9C0')
    frameMenu.place(x=0, y=0)

    welcomeLabel = Label(text="Welcome to sensor server", bg='#84A9C0')
    welcomeLabel.place(x=200, y=25)

    def showClick():
        frameShow = Frame(window, width=600, height=600, bg='#84A9C0')
        frameShow.place(x=0, y=0)

        frameSShow = Frame(frameShow, width=500, height=350)
        frameSShow.place(x=50, y=150)

        showLabel = Label(frameShow, text="Select the sensor you want to show", bg='#84A9C0')
        showLabel.place(x=50, y=50)

        exitButton = Button(frameShow, text="Exit", bg='#84A9C0', command=frameShow.destroy)
        exitButton.place(x=50, y=550)

    showButton = Button(frameMenu, text="Show", bg='#84A9C0', command=showClick)
    showButton.place(x=100, y=100)

    def hideClick():
        frameHide = Frame(window, width=600, height=600, bg='#84A9C0')
        frameHide.place(x=0, y=0)

        frameSHide = Frame(frameHide, width=500, height=350)
        frameSHide.place(x=50, y=150)

        hideLabel = Label(frameHide, text="Select the sensor you want to hide", bg='#84A9C0')
        hideLabel.place(x=50, y=50)

        exitButton = Button(frameHide, text="Exit", bg='#84A9C0', command=frameHide.destroy)
        exitButton.place(x=50, y=550)

    hideButton = Button(frameMenu, text="Hide", bg='#84A9C0', command=hideClick)
    hideButton.place(x=450, y=100)

    frameWeatherInfo = Frame(frameMenu, width=400, height=400)
    frameWeatherInfo.place(x=100, y=150)

    exitMainButton = Button(frameMenu, text="Log out", bg='#84A9C0', command=window.quit)
    exitMainButton.place(x=25, y=25)


if __name__ == "__main__":
    main()
