FROM maven:3.8.4-openjdk-17

ENV JAVA_VERSION=17
ENV GRAALVM_VERSION=22.0.0.2

RUN microdnf install gcc zlib-devel

# Install GraalVM
RUN curl -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAALVM_VERSION}/graalvm-ce-java${JAVA_VERSION}-linux-amd64-${GRAALVM_VERSION}.tar.gz > graalvm.tar.gz && \
	tar -zxf graalvm.tar.gz -C / && \
	rm graalvm.tar.gz

ENV JAVA_HOME="/graalvm-ce-java${JAVA_VERSION}-${GRAALVM_VERSION}"
ENV PATH="${JAVA_HOME}/bin:$PATH"

# Install native-image
RUN gu install native-image
