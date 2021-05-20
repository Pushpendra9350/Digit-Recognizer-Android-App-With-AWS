# MODEL TRAINING 
import tensorflow
import keras
import numpy as np
import matplotlib.pyplot as plt
import cv2
import pandas as pd
from PIL import Image
import base64
from keras.utils import np_utils
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Dense, Dropout, Activation, Flatten, Conv2D, MaxPooling2D
mnist = tensorflow.keras.datasets.mnist
(x_train,y_train),(x_test,y_test) = mnist.load_data()
# normalize inputs from 0-255 to 0-1
x_train = x_train / 255
x_test = x_test / 255
# one hot encode outputs
y_train = np_utils.to_categorical(y_train)
y_test = np_utils.to_categorical(y_test)
classes = y_test.shape[1]
x_trainr = np.array(x_train).reshape(-1,28,28,1)
x_testr = np.array(x_test).reshape(-1,28,28,1)
model = Sequential()
# https://www.pyimagesearch.com/2018/12/31/keras-conv2d-and-convolutional-layers/
# https://keras.io/api/layers/convolution_layers/convolution2d/ to know more
# 30 is number of filers/kernels we are using in this layer 
# (5,5) is the kernel size we are using here 
# input is to tell that get each image one by one 
model.add(Conv2D(30, (5, 5), input_shape=(28, 28, 1), activation='relu'))
# 2*2 maxpooling
model.add(MaxPooling2D(pool_size=(2, 2)))
model.add(Conv2D(15, (3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))
# To reduce 20% data
model.add(Dropout(0.2))
# flatten will make 28*28 to 784
model.add(Flatten())
# These are the dense layer same as MLP
model.add(Dense(128, activation='relu'))
model.add(Dense(50, activation='relu'))
 # Last layer with softmax activation
model.add(Dense(classes, activation='softmax'))
model.compile(loss = "categorical_crossentropy",optimizer="adam",metrics=["accuracy"])
model.fit(x_trainr,y_train,epochs=10,validation_data=(x_testr, y_test),batch_size=200,verbose=0)
val_loss, val_acc = model.evaluate(x_testr, y_test)
print(val_loss, val_acc)
model.save('Model.h5')