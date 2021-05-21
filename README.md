# Digit Recognizer Android Application deployed on AWS

## VIDEO DEMO: https://youtu.be/-VYZBYkXAh0
![Group 68](https://user-images.githubusercontent.com/43174363/119131906-a8180680-ba57-11eb-8093-8fd141c3996a.png)

## Table Of Content 
[Overview](#overview)<br>
[Motivation](#motivation)<br>
[Screenshots](#screenshots)<br>
[How to run it](#runit)<br>
[Technical Aspects](#tech)<br>
[Future Scope](#future)<br>
[References](#ref)<br>

<a name="overview"></a>
### Overview 
This is a complete Android application to recognize handwritten digits drawn on application itself.
Model is trained on CNN with 99.19% accuracy with optimized number of layers and parameters. 
User can further train this model on a new data on which this model will give wrong results.

<a name="motivation"></a>
### Motivation
Motive to develop this project:
* To Know how neural networks works in deep learning. 
* To know how CNN(Convolutional Neural networks work).
* To know how to connect and work together Android application and deep learning models.
* To know how to do optimization in deep learning regarding number of layer and optimizer.
* How to work with image data.
* how Deploy models on AWS.

<a name="screenshots"></a>
### Screenshots
<table>
<tr>
<td><img src = "https://user-images.githubusercontent.com/43174363/119126873-3d63cc80-ba51-11eb-9bc5-143aa0c599ab.jpeg"></td>
<td><img src = "https://user-images.githubusercontent.com/43174363/119126871-3ccb3600-ba51-11eb-9165-46b1f817c41c.jpeg"></td>
<td><img src = "https://user-images.githubusercontent.com/43174363/119126862-3a68dc00-ba51-11eb-8188-d5086e26bd5b.jpeg"></td>
<td><img src = "https://user-images.githubusercontent.com/43174363/119126869-3c329f80-ba51-11eb-9b78-c9742b1d5d10.jpeg"></td>
</tr>
</table>

<a name="runit"></a>
### How to run it
* First, download application from this git repository or from here https://drive.google.com/file/d/1AQM3X5W9hvqOL7Nxw1ckpN0W1amMcBX3/view?usp=sharing
* Then download files app.py, digitData.db, and Model.h5 and put them in a direcotry (in the same).
* Run python app.py (Make sure all libraries should be installed in your system)
* Put the link in the application and you are good to go.
* For demo watch video given above.
![Group 58](https://user-images.githubusercontent.com/43174363/119131866-a0f0f880-ba57-11eb-9450-e9b51f91ada4.png)


<a name="tech"></a>
### Technical Aspects

#### Modeling 
In this application we have used MNIST digit dataset <br>
**About Dataset**<br>
TRAIN.CSV: 60000 images to train model<br>
TEST.CSV: 10000 images to test trained model<br>
Each image size is 28*28
<br><br>
To download dataset go to: https://www.kaggle.com/oddrationale/mnist-in-csv<br>
Screensshot is given below<br><br>
<img width="597" alt="Screenshot 2021-05-21 at 4 48 41 PM" src="https://user-images.githubusercontent.com/43174363/119128903-deec1d80-ba53-11eb-882e-4626b38884ea.png">
<br><br>
* Analysis for optimize number of layers and best optimizer of CNN **MODEL** I have trained various model to get best results.
* Basically I developed 9 models 5 for layer optimization and 4 for get best optimizer for this problem. 
* Each model is run 5 times to get stable average result from earch model.
* Insigths of this analysis are given below 
<br>**screenshots**
<table>
<tr>
<td><img width="352" alt="Screenshot 2021-05-21 at 12 52 45 PM" src="https://user-images.githubusercontent.com/43174363/119129789-0394c500-ba55-11eb-8a68-c9477c7e526b.png"></td>
<td><img width="351" alt="Screenshot 2021-05-21 at 12 52 58 PM" src="https://user-images.githubusercontent.com/43174363/119129797-055e8880-ba55-11eb-8751-2efd14a80e00.png"></td>
<td><img width="348" alt="Screenshot 2021-05-21 at 12 53 16 PM" src="https://user-images.githubusercontent.com/43174363/119129807-07c0e280-ba55-11eb-9783-ba73c565cd3e.png">
</td>
</tr>
</table>
<br>
As we can clearly see that BLUE curve model is performing best in all three cases so we coose this model to continue.<br>
And summary of this neural network is given below and for more detailed analysis go to this .ipynb https://github.com/Pushpendra9350/Digit-Recognizer-Android-App-With-AWS/blob/master/CNNLayer_%26_Optimizer_finding.ipynb
<br><br>
<img width="552" alt="Screenshot 2021-05-21 at 2 39 14 PM" src="https://user-images.githubusercontent.com/43174363/119129817-0abbd300-ba55-11eb-9146-9116017144fa.png">
<br>
Optimizer Analysis is given below<br><br>
<img width="677" alt="Screenshot 2021-05-21 at 12 52 13 PM" src="https://user-images.githubusercontent.com/43174363/119129777-00013e00-ba55-11eb-8a0c-3398cb5c5587.png">
<br>
In this grapht we ca see ADAM is performing so we choose ADAM as our optimizer<br><br>

**For Android application Architechture** <br><br>
![Group 64](https://user-images.githubusercontent.com/43174363/119131886-a3ebe900-ba57-11eb-8b38-41ec91e26596.png)
<br><br>
**Technology, libraries, tools, and framework used**
* Python
* Flask
* Keras
* Neural Network
* Android Studio 
* Jupyter notebook
* google colab
* Java
* XML
* Numpy
* Pandas
* Matplotlib
* Tensorboard
* Tensorflow
* Joblib
* AWS - EC2
* etc

<a name="future"></a>
### Future Scope
* Extend this project to evaluate equations of maths.
* We can optimize this model with epoches, batch size, learning rate, dropout, maxpolling, and momentum.
* We can recognize multiple digits from only one image.
* We can add more classes like a to z also and 1 to 100 digits number 
* We can implement this model in the mobile with the help of tensorflow lite.

### THANKS FOR READING

<a name="ref"></a>
### References
https://demonuts.com/<br>
https://machinelearningmastery.com/

