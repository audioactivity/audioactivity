INCLUDE(FindPkgConfig)
PKG_CHECK_MODULES(PC_AUDIOACTIVITY audioactivity)

FIND_PATH(
    AUDIOACTIVITY_INCLUDE_DIRS
    NAMES audioactivity/api.h
    HINTS $ENV{AUDIOACTIVITY_DIR}/include
        ${PC_AUDIOACTIVITY_INCLUDEDIR}
    PATHS ${CMAKE_INSTALL_PREFIX}/include
          /usr/local/include
          /usr/include
)

FIND_LIBRARY(
    AUDIOACTIVITY_LIBRARIES
    NAMES gnuradio-audioactivity
    HINTS $ENV{AUDIOACTIVITY_DIR}/lib
        ${PC_AUDIOACTIVITY_LIBDIR}
    PATHS ${CMAKE_INSTALL_PREFIX}/lib
          ${CMAKE_INSTALL_PREFIX}/lib64
          /usr/local/lib
          /usr/local/lib64
          /usr/lib
          /usr/lib64
)

INCLUDE(FindPackageHandleStandardArgs)
FIND_PACKAGE_HANDLE_STANDARD_ARGS(AUDIOACTIVITY DEFAULT_MSG AUDIOACTIVITY_LIBRARIES AUDIOACTIVITY_INCLUDE_DIRS)
MARK_AS_ADVANCED(AUDIOACTIVITY_LIBRARIES AUDIOACTIVITY_INCLUDE_DIRS)
