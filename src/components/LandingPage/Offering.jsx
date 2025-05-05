
import React from 'react';

const Offering = () => {
  return (
    <div className="min-h-screen bg-gray-900 text-white flex flex-col items-center">
      {/* Header Section */}
      <header className="text-center py-10">
        <h1 className="text-4xl font-bold text-amber-400">OUR OFFERINGS</h1>
        <p className="text-lg text-gray-300 mt-2">Explore Our Comprehensive Services</p>
      </header>

      {/* Content Section */}
      <div className="flex justify-center items-center relative w-full h-[800px]">
        {/* Space for the Laptop and Plants Image */}
        <img
          src="/images/laptop.png"
          alt="Laptop and Plants"
          className="w-full h-full object-cover rounded-xl shadow-lg pointer-events-none"
          loading="lazy"
          draggable="false"
          onContextMenu={(e) => e.preventDefault()} // Prevent right-click
        />
        {/* Invisible Overlay to Prevent Interactions */}
        <div className="absolute inset-0 bg-transparent pointer-events-auto"></div>
      </div>
    </div>
  );
};

export default Offering;
