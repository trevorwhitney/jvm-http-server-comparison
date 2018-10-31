rootProject.name = "jvm-async-http-server-comparison"

include(":async:vert.x")
include(":async:netty")
include(":async:webflux")

include(":sync:builtin")
include(":sync:jetty")

include("components:logging")
include("components:prometheus")