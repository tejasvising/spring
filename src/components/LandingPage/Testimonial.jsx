import React from 'react';

const Testimonial = () => {
  return (
    <div className="min-h-screen bg-[#e6e6e6] relative overflow-hidden">
      {/* Header */}
      <div className="text-center py-12">
        <h1 className="text-5xl font-bold tracking-wide">TESTIMONIALS</h1>
      </div>

      {/* Curved Background Section */}
      <div className="relative">
        {/* Curved Background */}
        <div className="absolute w-full h-[800px] bg-[#1a1f24] transform -skew-y-6 origin-left translate-y-[-100px]" />

        

        {/* Content Container */}
        <div className="relative max-w-6xl mx-auto px-4 py-16">
          {/* Grid of Testimonials */}
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            {[...Array(6)].map((_, index) => (
              <div key={index} className="relative">
                {/* Speech Bubble */}
                <div className="bg-[#ffb84d] rounded-2xl p-6 min-h-[160px] relative">
                  {/* Triangle point for speech bubble */}
                  <div className="absolute bottom-0 left-6 w-4 h-4 bg-[#ffb84d] transform rotate-45 translate-y-2" />
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Testimonial;