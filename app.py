"""
@Author: Pushpendra Kumar
@version:1.2
"""
from keras.preprocessing.image import ImageDataGenerator, array_to_img, img_to_array, load_img
from flask import Flask,request
import tensorflow
import keras
import numpy as np
import matplotlib.pyplot as plt
import cv2
import sqlite3
import pandas as pd
from PIL import Image
import base64

# This is to give a name to our flask application
app = Flask(__name__)

# To load model from local memory
model = tensorflow.keras.models.load_model("Model.h5")

# api/result to send results back to database
# This is a post request
@app.route("/result",methods=["POST"])
def result():
    """
    If our prediction is wrong then we edit it accordingly
    """
    # To get data which is send by frontend
    result_data = request.get_json()
    correct_result = result_data["result"]
    result_type = result_data["type"]

    # if prediction is wrong then with code will execute 
    if result_type == "wrong":
        # To make connection with the database 
        connection = sqlite3.connect("digitData.db",detect_types=sqlite3.PARSE_DECLTYPES)
        cursor = connection.cursor()

        # To update our result label
        update_query="""UPDATE digits SET label = ? WHERE label=?"""
        update_data = (correct_result,"know")
        cursor.execute(update_query,update_data)
        cursor.close()
        connection.commit()
        connection.close()
    # if prediction is correct the we delete the data we have saved 
    elif result_type == "correct":
        connection = sqlite3.connect("digitData.db",detect_types=sqlite3.PARSE_DECLTYPES)
        cursor = connection.cursor()

        # To delete the row where we write know
        update_query="""DELETE FROM digits WHERE label='know'"""
        cursor.execute(update_query)
        cursor.close()
        connection.commit()
        connection.close()
    return "Done"

# This is a main url for prediction of results
# This is also a post request 
@app.route("/predict",methods=["POST"])
def predict():
    """
    We get an image in the for of base64 string the we will 
    process it then make predictions 
    """
    # To read image
    idata = request.get_json()
    # Decode that base64 image to bytes
    imgdata = base64.b64decode(idata["image"])
    filename = 'coming_image.jpg'
    # Write image in to a image file named as comming_image.jpg
    with open(filename, 'wb') as f:
        x = f.write(imgdata)
    f.close()

    # To read that comming image to save itinto tha database
    with open(filename, 'rb') as f:
        binarydata = f.read()
    connection = sqlite3.connect("digitData.db",detect_types=sqlite3.PARSE_DECLTYPES)
    cursor = connection.cursor()
    # To save image as a binary string into blob format into database
    cursor.execute("INSERT INTO digits (image,label) VALUES(?,?)",(binarydata,"know"))
    cursor.close()
    connection.commit()
    connection.close()

    # open image as a PIL image 
    col = Image.open(filename)
    gray = col.convert('L')

    # To convert 0 and 1 to 255 and 0
    bw = gray.point(lambda x: 0 if x<100 else 255, '1')

    # Save image a bw_image.jpg file 
    bw.save("bw_image.jpg")
    
    # Now conver (800,800,3) image to (800,800) and also grayscale
    img_array = cv2.imread("bw_image.jpg", cv2.IMREAD_GRAYSCALE)

    # To make reverse in the pixels
    img_array = cv2.bitwise_not(img_array)
    img_size = 28

    # Resize image from 800,800 to 28,28
    new_array = cv2.resize(img_array, (img_size,img_size))
    
    # To make normalize 
    user_test = tensorflow.keras.utils.normalize(new_array, axis = 1)

    # Reshape it to (1,28,28,1)
    user_test = user_test.reshape(-1, 28,28,1)

    # Make predictions
    predicted = model.predict(user_test)

    # Return the results
    return str(np.argmax(predicted[0]))

# This is an api to train a model on new dataset
@app.route("/train",methods=["GET"])
def train():
    """
    With this function we can train our model on the new dataset
    """
    connection = sqlite3.connect("digitData.db",detect_types=sqlite3.PARSE_DECLTYPES)
    cursor = connection.cursor()
    cursor.execute("select count(*) from digits")
    count = cursor.fetchone()[0]
    
    # Checking for records we have or not in database
    if count>0:

        # Fetch all the results 
        cursor.execute("select * from digits")
        all_images = cursor.fetchall()
        i = 0
        y = 0

        # Loop over the images 
        for image in all_images:
            if y==0:
                new_y = np.array(int(image[1]))
            else:
                new_y = np.append(new_y,int(image[1]))
            y+=1

            # Path to save new read image 
            filename3 = "bw_image1.jpg"
            with open(filename3, 'wb') as file:
                file.write(image[0])
            
            # Same as previous read the image file as PIL image 
            col = Image.open(filename3)
            gray = col.convert('L')

            # To convert 0 and 1 to 255 and 0
            bw = gray.point(lambda x: 0 if x<100 else 255, '1')

            # To save it again into black and white color
            bw.save("bw_image.jpg")

            # IMAGE AUGMENTATION to generate new images based in the current image 
            # Here are making 21 image base on each image 
            datagen = ImageDataGenerator(
                        rotation_range=40, # rotation
                        width_shift_range=0.2, # horizontal shift
                        height_shift_range=0.2, # vertical shift
                        zoom_range=0.2, # zoom
                        shear_range= 0.2, # Shearing
                        horizontal_flip=True, # horizontal flip
                        fill_mode="nearest") # brightness

            # Now read that we have written in black and white pixels 
            main_image = load_img('bw_image.jpg')

            # Convert it into an array 
            image_array = img_to_array(main_image)

            # Reshape (800,800,3) --> (1,800,800,3)
            image_array = image_array.reshape((1,) + image_array.shape)
            counts = 0

            # To get 21 images from that image generator
            for image_batch in datagen.flow(image_array, batch_size=1):
                i += 1
                counts += 1
                #generated_image.append(batch)
                image_batch = image_batch.reshape(800,800,3)

                # Change into grayscale image (800,800)
                img_array = cv2.cvtColor(image_batch, cv2.COLOR_BGR2GRAY)

                # Resize into a 28,28
                new_array = cv2.resize(img_array, (28,28))
                # To save that array into a big array to make dataset for training
                if i==1:
                    array = np.array(new_array)
                else:
                    array = np.append(array,new_array,axis=0)
                if counts > 20:
                    break 
                else:
                    new_y = np.append(new_y,int(image[1]))
        dim = int(((array.shape[0])//28))

        # Resize dataset ndarray
        new_train = array.reshape(dim,28,28)

        # Normalize training dataset
        new_train = tensorflow.keras.utils.normalize(new_train,axis=1)

        # Reshape it as (1,28,28,1)
        new_trainr = np.array(new_train).reshape(-1,28,28,1)

        # Compile our model this model is previous model which we have load earlier
        model.compile(loss = "sparse_categorical_crossentropy",optimizer="adam",metrics=["accuracy"])
        
        # Fit our model
        model.fit(new_trainr,new_y,epochs=5)

        # To save new model 
        model.save("Model.h5")

        # Now time to delete all images from database 
        cursor.execute("DELETE from digits")
        cursor.close()
        connection.commit()
        connection.close()
    return "Model is Trained"
if __name__ == "__main__":
    app.run(debug=True,host="0.0.0.0",port="5000")
