import numpy as np

import ServerHandler
import NewGUI
import matplotlib.pyplot as plt


class Plot:
    def __init__(self, Map):
        self.Map = Map
        xpoints = np.array([1, 8])
        ypoints = np.array([3, 10])

        plt.plot(xpoints, ypoints)
        plt.show()


if __name__ == "__main__":
    plot = Plot("bob")
