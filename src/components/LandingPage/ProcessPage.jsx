import React from 'react';

const Processpage = () => {
  return (
    <div className="min-h-screen bg-[#e6e6e6] p-8">
      <div className="max-w-6xl mx-auto">
        {/* Header Section */}
        <div className="text-center mb-16">
          <h1 className="text-5xl font-bold mb-6 tracking-wide">THE PROCESS</h1>
          <p className="text-xl max-w-4xl mx-auto leading-normal">
            Learn how our personalized college recommendations can, help you achieve your academic and career goals
          </p>
        </div>

        {/* Cards Grid */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-16">
          {[1, 2, 3].map((index) => (
            <div 
              key={index}
              className="bg-[#1a1f24] rounded-xl aspect-square relative p-6"
            >
              <div className="w-24 h-24 bg-[#ffb84d] rounded-full absolute top-6 left-6" />
            </div>
          ))}
        </div>

        {/* Download Button */}
        <div className="text-center">
          <button className="bg-[#ffb84d] text-black font-bold py-4 px-12 rounded-full text-lg">
            DOWNLOAD REPORT
          </button>
        </div>
      </div>
    </div>
  );
};

export default Processpage;