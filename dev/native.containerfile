FROM maven:3.8.3-jdk-11

ENV JAVA_VERSION=11
ENV GRAALVM_VERSION=22.0.0.2

RUN export DEBIAN_FRONTEND=noninteractive \
	&& \
	apt-get update -qq \
	&& \
	apt-get install -y \
		build-essential \
		libz-dev \
		zlib1g-dev \
	&& \
    apt-get clean \
    && \
    rm -rf /var/lib/apt/lists/*

# Install GraalVM
RUN curl -L https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-${GRAALVM_VERSION}/graalvm-ce-java${JAVA_VERSION}-linux-amd64-${GRAALVM_VERSION}.tar.gz > graalvm.tar.gz \
	&& \
	tar -zxf graalvm.tar.gz -C / \
	&& \
	rm graalvm.tar.gz

ENV JAVA_HOME="/graalvm-ce-java${JAVA_VERSION}-${GRAALVM_VERSION}"
ENV PATH="${JAVA_HOME}/bin:$PATH"

# Install native-image
RUN gu install native-image
