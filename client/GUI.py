import pickle
from tkinter import *
import socket


def window():
    window = Tk()
    window.title("Totally legit sensor app")
    window.geometry("600x600")
    window.configure(bg='#2F2D2E')

    log_in_frame(window)

    window.mainloop()


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


def MainMenu(window, s):

    s.send('getids\n'.encode('utf-8'))

    recv = s.recv(1024).decode("utf-8")
    dataRec = recv[recv.find(":") + 1:]
    ShowableData = dataRec.split(" ")
    print(ShowableData)

    # Create a variable
    #myvar = [{'This': 'is', 'Example': 2}, 'of', 'serialisation', ['using', 'pickle']]
    # Open a file and use dump()
    #with open('file.pkl', 'wb') as file:
        # A new file will be created
        #pickle.dump(myvar, file)

    with open('show.pkl', 'rb') as file:
        showTest = pickle.load(file)
        print(f"Show: {showTest}")

    with open('file.pkl', 'rb') as file:
        fileTest = pickle.load(file)
        print(f"File: {fileTest}")

    with open('hide.pkl', 'rb') as file:
        hideTest = pickle.load(file)
        print(f"Hide: {hideTest}")

    showData = []
    hideData = []

    for item in showTest:
        showData.append(item)

    for item in hideTest:
        hideData.append(item)

    for item in fileTest:
        showData.append(item)

    showData = list(dict.fromkeys(showData))

    #id 1 2 3 4
    id = 4
    amount = 1

    # new canvas
    frameMenu = Frame(window, width=600, height=600)
    frameMenu.configure(bg='#84A9C0')
    frameMenu.place(x=0, y=0)

    frameWeatherInfo = Frame(frameMenu, width=400, height=400)
    frameWeatherInfo.place(x=100, y=150)

    welcomeLabel = Label(text="Welcome to sensor server", bg='#84A9C0')
    welcomeLabel.place(x=200, y=25)

    sensorLabel = Label(frameWeatherInfo, font=("Courier", 18), text="")
    sensorLabel.place(x=0, y=175)

    idLabel = Label(frameWeatherInfo, text=f"{id} sensor:")
    idLabel.place(x=150, y=100)

    def getData():
        s.send(f'getdata {id} {amount}\n'.encode('utf-8'))

        recv = s.recv(1024).decode("utf-8")
        data = recv[recv.find(":") + 1:]

        disallowed_charachters = "{}"
        for charachters in disallowed_charachters:
            data = data.replace(charachters, "")

        cleanData = data.strip()
        sensorLabel.configure(text=cleanData)
        frameWeatherInfo.after(5000, getData)

    getData()

    def showClick():

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

        #exitButton = Button(frameShow, text="Exit", bg='#84A9C0', command=frameShow.destroy)
        #exitButton.place(x=50, y=550)

        # list box
        showListBox = Listbox(frameSShow, width=250, height=350)
        showListBox.place(x=0, y=0)

        hideListBox = Listbox(frameSShow, width=250, height=350)
        hideListBox.place(x=250, y=0)

        errorLabel = Label(frameShow, text='', bg='#84A9C0')
        errorLabel.place(x=300, y=550)

        #id list box -------------------------------------------

        for item in showData:
            showListBox.insert(END, item)

        for item in hideData:
            hideListBox.insert(END, item)
        #------------------------------------------------------------------

        def hideIt():
            if len(showListBox.curselection()) != 0:
                x = showListBox.get(showListBox.curselection())

                hideData.append(x)
                showData.remove(x)

                hideListBox.insert(END, x)
                errorLabel.configure(text='')
                showListBox.delete(ANCHOR)
            else:
                errorLabel.configure(text="Select an element first")

        toHideButton = Button(frameShow, text="Hide", command=hideIt, bg='#84A9C0')
        toHideButton.place(x=150, y=500)

        def showIt():
            if len(hideListBox.curselection()) != 0:
                x = hideListBox.get(hideListBox.curselection())

                showData.append(x)
                hideData.remove(x)

                showListBox.insert(END, x)
                errorLabel.configure(text='')
                hideListBox.delete(ANCHOR)
            else:
                errorLabel.configure(text="Select an element first")

        toShowButton = Button(frameShow, text="Show", command=showIt, bg='#84A9C0')
        toShowButton.place(x=400, y=500)

        def saveIt():
            errorLabel.configure(text="Saved", fg='green', bg='#84A9C0')
            saveButton['state'] = DISABLED
            #exitButton['state'] = DISABLED
            toShowButton['state'] = DISABLED
            toHideButton['state'] = DISABLED

            with open('show.pkl', 'wb') as file:
                pickle.dump(showData, file)

            with open('hide.pkl', 'wb') as file:
                pickle.dump(hideData, file)

            window.after(2000, frameShow.destroy)

        saveButton = Button(frameShow, text="Save", command=saveIt, bg='#84A9C0')
        saveButton.place(x=500, y=550)

    showButton = Button(frameMenu, text="Show", bg='#84A9C0', command=showClick)
    showButton.place(x=100, y=100)

    def exitClick():
        s.close()
        window.quit()

    exitMainButton = Button(frameMenu, text="Log out", bg='#84A9C0', command=exitClick)
    exitMainButton.place(x=25, y=25)


if __name__ == "__main__":
    window()
