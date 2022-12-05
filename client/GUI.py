from ServerHandler import ServerHandler
import Plot
from tkinter import *
import pickle


class GUI():

    # GUI constructor
    def __init__(self):
        self.serverHandler = ServerHandler('localhost')
        self.counter = 0
        # self.serverHandler.connect()

    # main method
    def main(self):
        GUI().window()

    # Creates a window
    def window(self):
        # This has to be between here and the first call to ServerHandler.login
        # Otherwise the socket will just not send anything
        self.serverHandler.connect()
        window = Tk()
        window.title("Totally legit sensor app")
        window.geometry("600x600")
        window.configure(bg='#2F2D2E')

        self.log_in_frame(window)

        window.mainloop()

    # Log in screen
    def log_in_frame(self, window):

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

        # Log in button action
        def myClick():
            username = usernameTextBox.get()
            password = passwordTextBox.get()

            if not username or not password:
                myLabel.configure(text="Please enter your username and password", fg='red', bg='#84A9C0')
                myLabel.place(x=110, y=150)
            else:

                recv = self.serverHandler.login(username, password)  # Sends username and password to server and receives the ack
                # print(recv)

                # If the username and password is incorrect the server will send 'failed'
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
                    window.after(2000, lambda: self.mainMenu(window))  # calls mainMenu methods after 2 seconds

        log_in = Button(frame1, text="Log in", command=myClick, bg='#84A9C0')
        log_in.place(x=380, y=150)

        exit_button = Button(frame1, text="Exit", command=window.quit, bg='#84A9C0')
        exit_button.place(x=50, y=150)

    # Creates Main menu
    def mainMenu(self, window):
        print("refreshed Main menu")
        counter = 0

        recv = self.serverHandler.getIDS()  # Sends a requests to get IDs from server using server handler
        dataRec = recv[recv.find(":") + 1:]
        everyID = dataRec.split(" ")

        # reads the file 'fide.pkl'
        # If the program is opened first time or there is no file called 'fide.pkl', it creates it
        try:
            with open('hide.pkl', 'rb') as file:
                pickedID = pickle.load(file)
        except:
            with open('hide.pkl', 'w'):
                pickedID = []

        # Removes IDs in everyID that are located in the 'hide.pkl'
        for i in pickedID:
            for j in everyID:
                if i == j:
                    everyID.remove(i)

        print(f"shown ids amount: {len(everyID)}, ids = {everyID}")
        amount = 1

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

        # Next button action
        # Pressing the button will change the sensor that will be displayed in main menu, in grey box
        def goNext():
            try:
                if len(everyID) < self.counter + 2:
                    self.counter = 0
                else:
                    self.counter = self.counter + 1

                nextID = everyID[self.counter]
                idLabel.configure(text=f"Sensor ID: {nextID}")
                print(f"Pressed: {self.counter}, sensor {nextID}")

                recv = self.serverHandler.getData(nextID, amount)  # Sends a request to a server for data,for a specific ID
                data = recv[recv.find(":") + 1:]

                # Filters the string so it is cleaner to use
                disallowed_characters = "{}"
                for characters in disallowed_characters:
                    data = data.replace(characters, "")

                cleanData = data.strip()
                sensorLabel.configure(text=cleanData)

            # If there is no sensor picked to be visible, then this line will be executed
            except:
                idLabel.configure(text=f"No sensor picked")

        # Refreshes data every 5 seconds
        def getData():
            # print(f"ID: {everyID[self.counter]}, counter: {self.counter}")
            recv = self.serverHandler.getData(everyID[counter], amount)  # Sends a request to a server for data,for a specific ID
            data = recv[recv.find(":") + 1:]

            # Filters the string so it is cleaner to use
            disallowed_characters = "{}"
            for characters in disallowed_characters:
                data = data.replace(characters, "")
            cleanData = data.strip()

            sensorLabel.configure(text=cleanData)
            idLabel.configure(text=f"Sensor ID: {everyID[self.counter]}")
            frameWeatherInfo.after(5000, getData)  # refreshes every 5 seconds

        try:
            getData()
        except:
            # If there is no sensor picked to be visible, then this line will be executed
            print("error because no ID was picked")
            idLabel.configure(text=f"No sensor picked")

        nextButton = Button(frameMenu, text="Next", bg='#84A9C0', command=goNext)
        nextButton.place(x=270, y=100)

        welcomeLabel = Label(text="Welcome to sensor server", bg='#84A9C0')
        welcomeLabel.place(x=200, y=25)

        # ------------------------------------------------------------------------------

        showButton = Button(frameMenu, text="Show", bg='#84A9C0', command=lambda: self.showClick(window, frameMenu))
        showButton.place(x=100, y=100)

        # ------------------------------------------------------------------------------

        # Main menu exit button action
        def exitClick():
            self.serverHandler.closeSocket()
            window.quit()

        exitMainButton = Button(frameMenu, text="Log out", bg='#84A9C0', command=exitClick)
        exitMainButton.place(x=25, y=25)

        # ------------------------------------------------------------------------------

        # plot button action. Creates a plot and saves it in a file
        def plotClick():
            plotData = []
            plotTime = []

            exitMainButton['state'] = DISABLED
            showButton['state'] = DISABLED
            nextButton['state'] = DISABLED
            plotButton['state'] = DISABLED

            # Creates a plot for every ID that is visible
            for item in everyID:
                recv = self.serverHandler.getRange(item)  # Sends a request to a server to get range for a specific sensor
                rangeAmount = recv[recv.find(":") + 1:]

                recv2 = self.serverHandler.getData(item, rangeAmount)  # Sends a request to a server to get a specific amount of data from a specific sensor
                rangeStuff = recv2[recv2.find(":") + 1:]
                print(f"ID: {item}, range amount {rangeAmount}, Data: {rangeStuff}")

                cleanRangeData = []

                # GUI receives a specific amount of data as a one string. Need to split to put it into an array
                rangeStuffSplit = rangeStuff.splitlines()
                # Removes '{},' from the string
                for j in rangeStuffSplit:

                    disallowed_characters = "{},"
                    for characters in disallowed_characters:
                        j = j.replace(characters, "")
                    individualCleanRangeData = j.strip()

                    cleanRangeData.append(individualCleanRangeData)

                # Somehow server sends last and first data without a random number, so removing it is an option
                try:
                    cleanRangeData.pop(int(rangeAmount) + 1)
                except:
                    print("Last one has no empty element in array")
                cleanRangeData.pop(0)

                # print(f"individual? {cleanRangeData}")
                veryCleanRangeData = []
                veryCleanRangeTime = []

                # Cleans the data, for example: "2022/11/29 - 15:30:30":"-8" turns into -8
                for v in cleanRangeData:
                    individualVeryCleanData = ""
                    individualVeryVeryCleanData = ""
                    for i in range(0, len(v)):
                        if i > 24:
                            individualVeryCleanData = individualVeryCleanData + v[i]

                    # individualVeryCleanData.replace('"', '')

                    if len(individualVeryCleanData) == 4:
                        individualVeryVeryCleanData = individualVeryVeryCleanData + individualVeryCleanData[0] + \
                                                      individualVeryCleanData[1] + individualVeryCleanData[2]
                    elif len(individualVeryCleanData) == 3:
                        individualVeryVeryCleanData = individualVeryVeryCleanData + individualVeryCleanData[0] + \
                                                      individualVeryCleanData[1]
                    elif len(individualVeryCleanData) == 2:
                        individualVeryVeryCleanData = individualVeryVeryCleanData + individualVeryCleanData[0]
                    else:
                        individualVeryVeryCleanData = "0"

                    # print(f"individual fdata: {individualVeryVeryCleanData}")
                    veryCleanRangeData.append(int(individualVeryVeryCleanData))

                print(f"Clean data: {veryCleanRangeData}")

                # Cleans the data, for example: "2022/11/29 - 15:30:30":"-8" turns into 2022/11/29 - 15:30:30
                for b in cleanRangeData:
                    individualVeryCleanTime = ""
                    individualVeryVeryCleanTime = ""

                    for i in range(0, len(b)):
                        if i > 0:
                            if i < 22:
                                individualVeryCleanTime = individualVeryCleanTime + b[i]

                    veryCleanRangeTime.append(individualVeryCleanTime)

                print(f"Clean time: {veryCleanRangeTime}")

                plot = Plot.Plot(veryCleanRangeData, veryCleanRangeTime, item)

            self.serverHandler.closeSocket()  # closes a socket
            window.quit()

        plotButton = Button(frameMenu, text="Plot", bg='#84A9C0', command=plotClick)
        plotButton.place(x=450, y=100)

    # Opens the sensor list frame
    def showClick(self, window, frameMenu):
        print("refreshed show menu")

        frameShow = Frame(window, width=600, height=600, bg='#84A9C0')
        frameShow.place(x=0, y=0)

        frameSShow = Frame(frameShow, width=500, height=350)
        frameSShow.place(x=50, y=150)

        serverListLabel = Label(frameShow, text="Select the sensor you want to:", bg='#84A9C0')
        serverListLabel.place(x=50, y=50)

        showLabel = Label(frameShow, text="Showing", bg='#84A9C0')
        showLabel.place(x=50, y=125)

        hideLabel = Label(frameShow, text="Hiding", bg='#84A9C0')
        hideLabel.place(x=300, y=125)

        # Sensor list exit button
        def exitShow():
            frameShow.destroy()
            frameMenu.destroy()
            self.mainMenu(window)

        exitButton = Button(frameShow, text="Exit", bg='#84A9C0', command=exitShow)
        exitButton.place(x=50, y=550)

        # -----------------------------------------------------------------------------------

        # hide list box
        hideListBox = Listbox(frameSShow, width=30, height=15)
        hideListBox.place(x=250, y=0)

        # reads the file 'fide.pkl'
        # If the program is opened first time or there is no file called 'fide.pkl', it creates it
        try:
            with open('hide.pkl', 'rb') as file:
                hiddenData = pickle.load(file)
        except:
            with open('hide.pkl', 'w'):
                pass
                hiddenData = []

        # Puts ID from file to a Hidden sensor list
        for item in hiddenData:
            hideListBox.insert(END, item)

        # -----------------------------------------------------------------------------------

        showListBox = Listbox(frameSShow, width=30, height=15)
        showListBox.place(x=0, y=0)

        recv = self.serverHandler.getIDS()  # Sends a request to a server for connected sensor IDs
        dataRec = recv[recv.find(":") + 1:]
        showableData = dataRec.split(" ")

        # Removes IDs in showableData that are same with hiddenData IDs
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

            # writes the changes in file using pickle
            with open('hide.pkl', 'wb') as file:
                pickle.dump(hideListBox.get(0, END), file)

            print(f"Hiding: {hideListBox.get(0, END)}")

            window.after(2000, frameShow.destroy)
            window.after(2000, frameMenu.destroy)
            window.after(2000, self.mainMenu(window))

        saveButton = Button(frameShow, text="Save", command=saveIt, bg='#84A9C0')
        saveButton.place(x=500, y=550)


if __name__ == "__main__":
    GUI().main()
