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
    s.send('getids\n'.encode('utf-8'))

    recv = s.recv(1024).decode("utf-8")
    dataRec = recv[recv.find(":") + 1:]
    showableData = dataRec.split(" ")

    # id 1 2 3 4
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

    # refreshes data gotten from server every 5 sec
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

    # Opens the sensor list frame
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

        def exitShow():
            #print("exit running")
            #with open('hide.pkl', 'rb') as file:
            #    hideData = pickle.load(file)

            #s.send('getids\n'.encode('utf-8'))

            #recv = s.recv(1024).decode("utf-8")
            #dataRec = recv[recv.find(":") + 1:]
            #showableData = dataRec.split(" ")
            #print(showableData)

            frameShow.destroy()

        exitButton = Button(frameShow, text="Exit", bg='#84A9C0', command=exitShow)
        exitButton.place(x=50, y=550)

        # list box
        showListBox = Listbox(frameSShow, width=250, height=350)
        showListBox.place(x=0, y=0)

        hideListBox = Listbox(frameSShow, width=250, height=350)
        hideListBox.place(x=250, y=0)

        errorLabel = Label(frameShow, text='', bg='#84A9C0')
        errorLabel.place(x=300, y=550)

        try:
            with open('hide.pkl', 'rb') as file:
                hideData = pickle.load(file)
                #print(f"Hidden data: {hideData}")
        except:
            print("File is empty")
            with open('hide.pkl', 'wb') as file:
                print("file created")
                hideData = []

        for i in hideData:
            for j in showableData:
                if i == j:
                    showableData.remove(i)

        #print(f"Showing data: {showableData}")

        # id list box -------------------------------------------
        try:
            for item in hideData:
                hideListBox.insert(END, item)
        except:
            print("hidelistbox is empty")

        for item in showableData:
            showListBox.insert(END, item)

        # ------------------------------------------------------------------------------

        # button command to hide a specific sensor
        def hideIt():
            if len(showListBox.curselection()) != 0:
                x = showListBox.get(showListBox.curselection())

                hideData.append(x)
                hideListBox.insert(END, x)

                showableData.remove(x)
                showListBox.delete(ANCHOR)

                errorLabel.configure(text='')
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

                showableData.append(x)
                showListBox.insert(END, x)

                hideData.remove(x)
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

            print(f"Hidden data saved: {hideData}")
            print(f"Showing data saved: {showableData}")

            with open('hide.pkl', 'wb') as file:
                pickle.dump(hideData, file)


            window.after(2000, frameShow.destroy)

        saveButton = Button(frameShow, text="Save", command=saveIt, bg='#84A9C0')
        saveButton.place(x=500, y=550)

    # ------------------------------------------------------------------------------

    showButton = Button(frameMenu, text="Show", bg='#84A9C0', command=showClick)
    showButton.place(x=100, y=100)

    # ------------------------------------------------------------------------------

    # Exit button command
    def exitClick():
        s.close()
        window.quit()

    exitMainButton = Button(frameMenu, text="Log out", bg='#84A9C0', command=exitClick)
    exitMainButton.place(x=25, y=25)

    # ------------------------------------------------------------------------------


if __name__ == "__main__":
    window()
