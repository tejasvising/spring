import React, { useEffect, useState } from 'react';
import {
  LogOut,
  Download,
  ChevronDown,
  File,
  Home
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import { auth} from "../../Firebase/Firebase";
import {  onAuthStateChanged,signOut } from "firebase/auth";
 const Dashboard = () => {
   const [pdfDocuments, setPdfDocuments] = useState([]);
   const navigate = useNavigate();
   useEffect(() => {
    let isMounted = true; // flag to track if component is mounted

    fetchData();

    async function fetchData() {
        try {
            let user_uid = localStorage.getItem('userid');
            console.log("User signed in:", user_uid);
            console.log(`http://127.0.0.1:8080/pdfdownload?userID=${user_uid}`);

            const res = await fetch(`http://localhost:8080/pdfdownload?userID=${user_uid}`, {
                method: 'GET',
                credentials:'include',
                headers: {
                //  'Authorization': `Bearer ${localStorage.getItem('token')}`,
                  'Content-Type': 'application/json'
                },
            });

            const data = await res.json();

            if (!isMounted) return; // avoid state updates if unmounted

            setPdfDocuments([]); // reset before new data

            for (let i = 0; i < data.length; i++) {
                const byteCharacters = atob(data[i].file);
                const byteNumbers = new Array(byteCharacters.length);

                for (let j = 0; j < byteCharacters.length; j++) {
                    byteNumbers[j] = byteCharacters.charCodeAt(j);
                }

                const byteArray = new Uint8Array(byteNumbers);
                const blob = new Blob([byteArray], { type: "application/pdf" });
                const uri = URL.createObjectURL(blob);

                if (!isMounted) return;

                setPdfDocuments((prevDocs) => [
                    ...prevDocs,
                    { id: i + 1, name: data[i].filename, rank: "Neet", url: uri },
                ]);
            }
        } catch (error) {
            console.error("Error fetching PDFs:", error);
        }
    }

    return () => {
        isMounted = false; // set flag to false on unmount
    };
}, []);

  const [showDropdown, setShowDropdown] = useState(false);

  // Sample PDF data - in a real app, this would come from props or an API
 /* const pdfDocuments = [
    { id: 1, name: 'Annual Report 2024', rank: 'Confidential', url: '/path/to/pdf1' },
    { id: 2, name: 'Q1 Financial Statement', rank: 'Internal', url: '/path/to/pdf2' },
    { id: 3, name: 'Employee Handbook', rank: 'Public', url: '/path/to/pdf3' },
    { id: 4, name: 'Project Proposal', rank: 'Restricted', url: '/path/to/pdf4' }
  ];*/

  const handleLogout = async () => {
    // Implement logout logic here
    try {
      
      const res=await fetch('http://localhost:8080/api/auth/logout', {
        method: 'POST',
        credentials:'include',
        headers: {
          'Content-Type': 'application/json'
        },
        // body: JSON.stringify({
        //   "userid": localStorage.getItem('userid'),
        //  // "token": localStorage.getItem('token')
        // }),
      });
      const data= await res.text();
      console.log("logoutted: ",data);
      localStorage.removeItem('userid');
      localStorage.removeItem('token'); 
      localStorage.removeItem('email'); 
      navigate("/");

    }
      catch(e){
        console.log(e.message);
      }
    
    // 2. Clear frontend tokens
    // localStorage.removeItem('userid');
    // sessionStorage.removeItem('oauthState');
    // //signOut(auth);
    
    // console.log('Logging out...');
  };

  const handleDownload = (pdfUrl) => {
    // Implement PDF download logic here
    
    console.log('Downloading:', pdfUrl);
    window.open(pdfUrl);

  };

  const handleHome = () => {
    // Implement home navigation logic here
    navigate("/");
    console.log('Navigating to home...');
  };

  return (
    <div className="min-h-screen bg-gray-100">
      {/* Header with navigation */}
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto px-4 py-4 flex justify-between items-center">
          <div className="flex items-center space-x-6">
            <button
              onClick={handleHome}
              className="flex items-center space-x-2 text-gray-600 hover:text-gray-900"
            >
              <Home className="h-5 w-5" />
              <span className="font-medium">Home</span>
            </button>
            <h1 className="text-2xl font-bold text-gray-800">Document Dashboard</h1>
          </div>
          <div className="relative">
            <button
              onClick={() => setShowDropdown(!showDropdown)}
              className="flex items-center space-x-2 bg-white p-2 rounded-lg hover:bg-gray-50"
            >
              <div className="h-8 w-8 rounded-full bg-blue-500 flex items-center justify-center">
                <span className="text-white font-medium">U</span>
              </div>
              <ChevronDown className="h-4 w-4 text-gray-500" />
            </button>
           
            {showDropdown && (
              <div className="absolute right-0 mt-2 w-48 bg-white rounded-md shadow-lg py-1">
                <button
                  onClick={handleLogout}
                  className="flex items-center px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 w-full"
                >
                  <LogOut className="h-4 w-4 mr-2" />
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </header>

      {/* Main content */}
      <main className="max-w-7xl mx-auto px-4 py-8">
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {pdfDocuments.map((doc) => (
            <div
              key={doc.id}
              className="bg-white rounded-lg shadow-md p-6 flex flex-col"
            >
              <div className="flex-1">
                <div className="flex items-center justify-center h-32 bg-gray-50 rounded-lg mb-4">
                  <File className="h-16 w-16 text-gray-400" />
                </div>
                <h3 className="text-lg font-medium text-gray-900 mb-1">{doc.name}</h3>
                <span className="inline-block px-2 py-1 text-sm rounded-full bg-blue-100 text-blue-800 mb-4">
                  {doc.rank}
                </span>
              </div>
              <button
               
                onClick={() => handleDownload(doc.url)}
                className="flex items-center justify-center px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
              >
                <Download className="h-4 w-4 mr-2" />
                Download PDF
              </button>
            </div>
          ))}
        </div>
      </main>
    </div>
  );
};

export default Dashboard;