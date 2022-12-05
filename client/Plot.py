import matplotlib.pyplot as plt


class Plot:

    # Plot constructor
    def __init__(self, data, time, id):
        self.data = data
        self.time = time
        self.id = id
        self.MaxPoints = 10

        Fig, ax = plt.subplots()
        ax.plot(time, data)
        ax.set_xlabel("Time")
        ax.set_ylabel("data")
        ax.set_title(f'ID: {id} plot')
        # labels = ax.get_xticklabels()

        #if len(labels)>self.MaxPoints:
        #    #diff = len(labels)-self.MaxPoints
        #    #choice = np.random.choice(np.linspace(0, len(labels), len(labels), endpoint=False), size=self.MaxPoints, replace=False)
        #    choice = np.random.choice(len(labels), size=self.MaxPoints, replace=False)
        #    print(f'Labels are: {labels}')
        #    labels = np.take(labels, choice)
        #    print(f'New labels are: {labels}')
        #ax.set_xticklabels(labels, rotation=90, fontsize=11)

        ax.tick_params("x", labelrotation=90, labelsize=11)
        # ax.set_xticks(np.arange(0, max(time), 5), rotation=90, fontsize1=11)
        print(f'xticks are: {ax.get_xticklabels()}')
        Fig.savefig(f'plot_figure_{id}.png')

        #plt.plot(time, data)
        #plt.xlabel('Time')
        #plt.ylabel('data')
        #plt.title(f'ID: {id} plot')
        #plt.xticks(rotation=90, fontsize=11)
        #plt.set_xticks(np.arange(0, max(time), 5))
        #plt.savefig(f'plot_figure_{id}.png')
        #plt.show()