from django.http import HttpResponse
from django.template import loader

def helloworld(request):
  template = loader.get_template('myfirst.html')
  return HttpResponse(template.render())
def creationCircuit(request):
  template = loader.get_template('creationcircuit.html')
  return HttpResponse(template.render())
def homepage(request):
  template = loader.get_template('homepage.html')
  return HttpResponse(template.render())