from django.urls import path
from . import views

urlpatterns = [
    path('creationcircuit/', views.creationCircuit, name='creationCircuit'),
    path('homepage/', views.homepage, name='homepage'),
    path('helloworld/', views.helloworld, name='helloworld'),
]