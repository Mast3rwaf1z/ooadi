import numpy as np

import ServerHandler
import NewGUI
import matplotlib.pyplot as plt


class Plot:
    def __init__(self, data, time):
        self.data = data
        self.time = time
        #xpoints = np.array([1, 8])
        #ypoints = np.array([3, 10])

        #plt.plot(xpoints, ypoints)
        #plt.show()

        for i in data:
            name = list(i.keys())
            values = list(i.values())
            for j in time:
                if i == j:
                    plt.plot()

if __name__ == "__main__":
    plot = Plot("bob")
