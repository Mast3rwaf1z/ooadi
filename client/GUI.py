import pickle
from tkinter import *
import socket

counter = 0


def window():
    window = Tk()
    window.title("Totally legit sensor app")
    window.geometry("600x600")
    window.configure(bg='#2F2D2E')

    log_in_frame(window)

    window.mainloop()


# Log in menu
def log_in_frame(window):
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

        if not username or not password:
            myLabel.configure(text="Please enter your username and password", fg='red', bg='#84A9C0')
            myLabel.place(x=110, y=150)
        else:
            s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
            s.connect(('localhost', 8000))
            s.send(f'login {username} {password}\n'.encode('utf-8'))

            recv = s.recv(1024).decode("utf-8")

            if recv == "failed":
                print("Failed to log in")
                myLabel.configure(text="Log in unsuccessful", fg='red', bg='#84A9C0')
                myLabel.place(x=150, y=150)
            else:
                myLabel.configure(text="Log in successful", fg='green', bg='#84A9C0')
                myLabel.place(x=150, y=150)
                window.after(2000, frame1.destroy)
                log_in['state'] = DISABLED
                exit_button['state'] = DISABLED
                window.after(2000, lambda: MainMenu(window, s))

    log_in = Button(frame1, text="Log in", command=myClick, bg='#84A9C0')
    log_in.place(x=380, y=150)

    exit_button = Button(frame1, text="Exit", command=window.quit, bg='#84A9C0')
    exit_button.place(x=50, y=150)


# Main menu
def MainMenu(window, s):
    print("refreshed Main menu")

    s.send('getids\n'.encode('utf-8'))

    recv = s.recv(1024).decode("utf-8")
    dataRec = recv[recv.find(":") + 1:]
    everyID = dataRec.split(" ")

    try:
        with open('hide.pkl', 'rb') as file:
            pickedID = pickle.load(file)
    except:
        with open('hide.pkl', 'w'):
            pass
            pickedID = []

    for i in pickedID:
        for j in everyID:
            if i == j:
                everyID.remove(i)

    print(f"shown ids amount: {len(everyID)}, ids = {everyID}")

    # id 1 2 3 4
    id = 1
    amount = 1
    global counter

    # new canvas
    frameMenu = Frame(window, width=600, height=600)
    frameMenu.configure(bg='#84A9C0')
    frameMenu.place(x=0, y=0)

    frameWeatherInfo = Frame(frameMenu, width=400, height=400)
    frameWeatherInfo.place(x=100, y=150)

    sensorLabel = Label(frameWeatherInfo, font=("Courier", 18), text="")
    sensorLabel.place(x=0, y=175)

    idLabel = Label(frameWeatherInfo, font=("Courier", 18), text="")
    idLabel.place(x=0, y=100)

    try:
        idLabel.configure(text=f"sensor ID: {everyID[counter]}")
    except:
        idLabel.configure(text="No IDs picked")

    def goNext():
        try:
            global counter
            counter = counter + 1
            print(f"Pressed: {counter}, sensor {everyID[counter]}")

            idLabel.configure(text=f"sensor ID: {everyID[counter]}")

            if len(everyID) - 1 == ++counter:
                counter = len(everyID) - 2 - counter

        except:
            print("Only one or no sensors are left")
            counter = 0

        s.send(f'getdata {everyID[counter]} {amount}\n'.encode('utf-8'))

        recv = s.recv(1024).decode("utf-8")
        data = recv[recv.find(":") + 1:]

        disallowed_charachters = "{}"
        for charachters in disallowed_charachters:
            data = data.replace(charachters, "")

        cleanData = data.strip()
        sensorLabel.configure(text=cleanData)
        frameWeatherInfo.after(5000, getData)

    # won't work
    def getData():
        s.send(f'getdata {everyID[counter]} {amount}\n'.encode('utf-8'))

        recv = s.recv(1024).decode("utf-8")
        data = recv[recv.find(":") + 1:]

        disallowed_charachters = "{}"
        for charachters in disallowed_charachters:
            data = data.replace(charachters, "")

        cleanData = data.strip()
        sensorLabel.configure(text=cleanData)
        frameWeatherInfo.after(5000, getData)

    try:
        getData()
    except:
        print("error because no ID was picked")

    nextButton = Button(frameMenu, text="Next", bg='#84A9C0', command=goNext)
    nextButton.place(x=270, y=100)

    welcomeLabel = Label(text="Welcome to sensor server", bg='#84A9C0')
    welcomeLabel.place(x=200, y=25)

    # ------------------------------------------------------------------------------

    showButton = Button(frameMenu, text="Show", bg='#84A9C0', command=lambda: showClick(s, window, frameMenu))
    showButton.place(x=100, y=100)

    # ------------------------------------------------------------------------------

    # Exit button command
    def exitClick():
        s.close()
        window.quit()

    exitMainButton = Button(frameMenu, text="Log out", bg='#84A9C0', command=exitClick)
    exitMainButton.place(x=25, y=25)

    # ------------------------------------------------------------------------------

    # Opens the sensor list frame


def showClick(s, window, frameMenu):
    print("refreshed show menu")

    frameShow = Frame(window, width=600, height=600, bg='#84A9C0')
    frameShow.place(x=0, y=0)

    frameSShow = Frame(frameShow, width=500, height=350)
    frameSShow.place(x=50, y=150)

    serverListLabel = Label(frameShow, text="Select the sensor you want to:", bg='#84A9C0')
    serverListLabel.place(x=50, y=50)

    showLabel = Label(frameShow, text="Show", bg='#84A9C0')
    showLabel.place(x=50, y=125)

    hideLabel = Label(frameShow, text="Hide", bg='#84A9C0')
    hideLabel.place(x=300, y=125)

    def exitShow():
        frameShow.destroy()
        frameMenu.destroy()
        MainMenu(window, s)

    exitButton = Button(frameShow, text="Exit", bg='#84A9C0', command=exitShow)
    exitButton.place(x=50, y=550)

    # -----------------------------------------------------------------------------------

    # hide list box
    hideListBox = Listbox(frameSShow, width=30, height=15)
    hideListBox.place(x=250, y=0)

    try:
        with open('hide.pkl', 'rb') as file:
            hiddenData = pickle.load(file)
    except:
        with open('hide.pkl', 'w'):
            pass
            hiddenData = []

    for item in hiddenData:
        hideListBox.insert(END, item)

    # -----------------------------------------------------------------------------------

    showListBox = Listbox(frameSShow, width=30, height=15)
    showListBox.place(x=0, y=0)

    s.send('getids\n'.encode('utf-8'))

    recv = s.recv(1024).decode("utf-8")
    dataRec = recv[recv.find(":") + 1:]
    showableData = dataRec.split(" ")

    for i in hiddenData:
        for j in showableData:
            if i == j:
                showableData.remove(i)

    for item in showableData:
        showListBox.insert(END, item)

    # -----------------------------------------------------------------------------------

    errorLabel = Label(frameShow, text='', bg='#84A9C0')
    errorLabel.place(x=300, y=550)

    # button command to hide a specific sensor
    def hideIt():
        if len(showListBox.curselection()) != 0:
            x = showListBox.get(showListBox.curselection())
            errorLabel.configure(text='')

            hideListBox.insert(END, x)
            showListBox.delete(ANCHOR)
        else:
            errorLabel.configure(text="Select an element first")

    toHideButton = Button(frameShow, text="Hide", command=hideIt, bg='#84A9C0')
    toHideButton.place(x=150, y=500)

    # ------------------------------------------------------------------------------

    # button command to show specific sensor
    def showIt():
        if len(hideListBox.curselection()) != 0:
            x = hideListBox.get(hideListBox.curselection())
            errorLabel.configure(text='')

            showListBox.insert(END, x)
            hideListBox.delete(ANCHOR)

        else:
            errorLabel.configure(text="Select an element first")

    toShowButton = Button(frameShow, text="Show", command=showIt, bg='#84A9C0')
    toShowButton.place(x=400, y=500)

    # ------------------------------------------------------------------------------

    # save the changes of what censors are hidden
    def saveIt():
        errorLabel.configure(text="Saved", fg='green', bg='#84A9C0')
        saveButton['state'] = DISABLED
        exitButton['state'] = DISABLED
        toShowButton['state'] = DISABLED
        toHideButton['state'] = DISABLED

        with open('hide.pkl', 'wb') as file:
            pickle.dump(hideListBox.get(0, END), file)

        print(f"Hiding: {hideListBox.get(0, END)}")

        window.after(2000, frameShow.destroy)
        window.after(2000, frameMenu.destroy)
        window.after(2000, MainMenu(window, s))

    saveButton = Button(frameShow, text="Save", command=saveIt, bg='#84A9C0')
    saveButton.place(x=500, y=550)


if __name__ == "__main__":
    window()