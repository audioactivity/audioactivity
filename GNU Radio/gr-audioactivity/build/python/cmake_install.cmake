# Install script for directory: /Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python

# Set the install prefix
if(NOT DEFINED CMAKE_INSTALL_PREFIX)
  set(CMAKE_INSTALL_PREFIX "/usr/local")
endif()
string(REGEX REPLACE "/$" "" CMAKE_INSTALL_PREFIX "${CMAKE_INSTALL_PREFIX}")

# Set the install configuration name.
if(NOT DEFINED CMAKE_INSTALL_CONFIG_NAME)
  if(BUILD_TYPE)
    string(REGEX REPLACE "^[^A-Za-z0-9_]+" ""
           CMAKE_INSTALL_CONFIG_NAME "${BUILD_TYPE}")
  else()
    set(CMAKE_INSTALL_CONFIG_NAME "Release")
  endif()
  message(STATUS "Install configuration: \"${CMAKE_INSTALL_CONFIG_NAME}\"")
endif()

# Set the component getting installed.
if(NOT CMAKE_INSTALL_COMPONENT)
  if(COMPONENT)
    message(STATUS "Install component: \"${COMPONENT}\"")
    set(CMAKE_INSTALL_COMPONENT "${COMPONENT}")
  else()
    set(CMAKE_INSTALL_COMPONENT)
  endif()
endif()

if(NOT CMAKE_INSTALL_COMPONENT OR "${CMAKE_INSTALL_COMPONENT}" STREQUAL "Unspecified")
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/python2.7/site-packages/audioactivity" TYPE FILE FILES
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python/__init__.py"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python/packet_utils.py"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python/fec.py"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python/errors.py"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/python/packet.py"
    )
endif()

if(NOT CMAKE_INSTALL_COMPONENT OR "${CMAKE_INSTALL_COMPONENT}" STREQUAL "Unspecified")
  file(INSTALL DESTINATION "${CMAKE_INSTALL_PREFIX}/lib/python2.7/site-packages/audioactivity" TYPE FILE FILES
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/__init__.pyc"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/packet_utils.pyc"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/fec.pyc"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/errors.pyc"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/packet.pyc"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/__init__.pyo"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/packet_utils.pyo"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/fec.pyo"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/errors.pyo"
    "/Users/markus/Campus/TUD/2014WS/Thesis/gr/gr-audioactivity/build/python/packet.pyo"
    )
endif()

