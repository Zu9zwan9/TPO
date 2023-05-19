TARGET := main
SRC_DIR := src
MY_PROJECT_BASE_DIRECTORY := /Users/mbard/Documents/Uni/Uni/Subjects/6rdSem/subjs/TPO/JavaLabs-master/src/com/lab4
SRC_DIR := $(MY_PROJECT_BASE_DIRECTORY)/src
BUILD_DIR := $(MY_PROJECT_BASE_DIRECTORY)/build

init:
	if [ -d $(BUILD_DIR) ]; then \
  		echo ; \
  	else \
  	  mkdir $(BUILD_DIR); \
  	fi && \
  	if [ -d $(SRC_DIR) ]; then \
  		echo ; \
    else \
  		mkdir $(SRC_DIR); \
  	fi
build_main:
	mpic++ -o $(BUILD_DIR)/$(TARGET).o $(SRC_DIR)/$(TASK)/$(TARGET).cpp
run:
	mpirun --hostfile hostfile $(BUILD_DIR)/$(TARGET).o
build_and_run:
	make init && make build_main TASk=$(TASK) && make run